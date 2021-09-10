package app.services

import app.data.entities.ExchangeEntity
import app.data.entities.ExchangeRequestEntity
import app.data.entities.changes.UserChangeEntity
import app.data.repositories.ExchangeRequestRepository
import app.data.repositories.ExchangesRepository
import app.services.exceptions.EntityNotFoundException
import app.services.exceptions.InvalidExchangeRequestException
import app.services.models.ExchangeRequestCreatedEvent
import app.services.models.ExchangeRequestStatusChangeEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import util.json
import java.sql.Timestamp
import java.time.LocalDateTime

@Service
class ExchangeService {

    companion object {
        private val log: Logger = LogManager.getLogger()
    }

    @Autowired
    private lateinit var exchangeRequestRepository: ExchangeRequestRepository

    @Autowired
    private lateinit var exchangeRepository: ExchangesRepository

    @Autowired
    private lateinit var occurrenceService: OccurrenceService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var lessonService: LessonService

    @Autowired
    private lateinit var userQueueService: UserQueueService

    @Autowired
    private lateinit var messagingTemplate: RabbitTemplate



    fun findAllExchanges() = exchangeRepository.findAll()

    fun findExchangesForUser(id: Int) = exchangeRepository.findChangesForUser(id)

    fun saveRequest(r: ExchangeRequestEntity) {
        exchangeRequestRepository.save(r)

        messagingTemplate.convertAndSend("request-send", ExchangeRequestCreatedEvent(
            r.id,
            r.fromUserId, r.fromLessonId, r.fromIndex,
            r.toUserId, r.toLessonId, r.toIndex,
            LocalDateTime.now()
        ))
    }

    fun findRequestById(id: Int) =
        exchangeRequestRepository
            .findById(id)
            .orElseThrow { throw EntityNotFoundException("exchange request") }

    fun findRequestsToUser(): Collection<ExchangeRequestEntity> =
        exchangeRequestRepository.findRequestsToUser(userService.currentlyAuthenticatedUserDetails.id).toList()

    fun findRequestByUser(): Collection<ExchangeRequestEntity> =
        exchangeRequestRepository.findRequestsByUser(userService.currentlyAuthenticatedUserDetails.id).toList()

    fun declineRequest(id: Int): Unit = declineRequest(findRequestById(id))

    @PreAuthorize("request.toUserId == userService.currentlyAuthenticatedUser.id")
    fun declineRequest(request: ExchangeRequestEntity): Unit {
        log.debug("Request ${request.json()} has been declined.")

        request.status = ExchangeRequestEntity.Status.DECLINED
        request.resolvementTime = Timestamp(System.currentTimeMillis())
        saveRequest(request)

        messagingTemplate.convertAndSend("request-state-change", ExchangeRequestStatusChangeEvent(
            LocalDateTime.now(),
            request.id,
            ExchangeRequestStatusChangeEvent.Status.valueOf(request.status.toString()),
        ))
    }

    fun acceptRequest(id: Int): Unit = acceptRequest(findRequestById(id))

    @PreAuthorize("request.toUserId == userService.currentlyAuthenticatedUser.id")
    fun acceptRequest(request: ExchangeRequestEntity): Unit {
        validateRequest(request)
        applyRequest(request)
    }

    private fun validateRequest(request: ExchangeRequestEntity) {

        if(request.status != ExchangeRequestEntity.Status.PENDING)
            handleInvalidRequest(request, "Request had status ${request.status}")

        if(request.fromUserId == request.toUserId) handleInvalidRequest(request, "You cannot send request to yourself")

        val fromLesson = lessonService.findLesson(request.fromLessonId)
        val toLesson = lessonService.findLesson(request.toLessonId)

        if(request.fromIndex < fromLesson.lessonIndex || request.toIndex < toLesson.lessonIndex)
            handleInvalidRequest(request, "Request expired")

        if(!occurrenceService.doesUserOccur(request.fromLessonId, request.fromIndex, request.fromUserId))
            handleInvalidRequest(request, "Request author does not occur as specified in request")

        if(!occurrenceService.doesUserOccur(request.toLessonId, request.toIndex, request.toUserId))
            handleInvalidRequest(request, "Request target does not occur as specified in request")

        log.debug("Request ${request.json()} passed validation")
    }

    private fun applyRequest(request: ExchangeRequestEntity) {
        log.debug("Applying request ${request.id}")

        val fromIndexChange = userQueueService.saveUserChange(
            UserChangeEntity(0, request.fromLessonId, request.fromIndex, request.toUserId, Timestamp(System.currentTimeMillis()))
        )

        val toDateChange = userQueueService.saveUserChange(
            UserChangeEntity(0, request.toLessonId, request.toIndex, request.fromUserId, Timestamp(System.currentTimeMillis()))
        )

        val exchange = ExchangeEntity(
            0,
            fromIndexChange.id,
            toDateChange.id,
            request.id
        )

        exchangeRepository.save(exchange)

        request.status = ExchangeRequestEntity.Status.ACCEPTED
        request.resolvementTime = Timestamp(System.currentTimeMillis())
        saveRequest(request)

        sendRequestStatus(request)
    }

    @PreAuthorize("request.toUserId == userService.currentlyAuthenticatedUser.id")
    fun handleInvalidRequest(request: ExchangeRequestEntity, message: String): Nothing {
            request.apply {
                log.debug("Exchange request with id $id was invalid. Error message: $message." + "Request: ${request.json()}")
            }

        request.status = ExchangeRequestEntity.Status.INVALID
        request.resolvementTime = Timestamp(System.currentTimeMillis())
        saveRequest(request)

        sendRequestStatus(request)

        throw InvalidExchangeRequestException(message)
    }

    private fun sendRequestStatus(request: ExchangeRequestEntity) =
        messagingTemplate.convertAndSend("request-state-change", ExchangeRequestStatusChangeEvent(
            LocalDateTime.now(),
            request.id,
            ExchangeRequestStatusChangeEvent.Status.valueOf(request.status.toString()),
        ))

}
