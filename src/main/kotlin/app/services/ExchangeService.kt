package app.services

import app.data.entities.ExchangeEntity
import app.data.entities.ExchangeRequestEntity
import app.data.entities.changes.DateChangeEntity
import app.data.repositories.ExchangeRequestRepository
import app.data.repositories.ExchangesRepository
import app.security.QueueUser
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.sql.Date
import java.time.LocalDate

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
    private lateinit var changeService: ChangeService

    @Autowired
    private lateinit var occurrenceService: OccurrenceService

    @Autowired
    private lateinit var userService: UserService


    fun findAllExchanges() = exchangeRepository.findAll()

    fun findExchangesForUser(id: Int) = exchangeRepository.findChangesForUser(id)

    fun saveRequest(request: ExchangeRequestEntity) =
        exchangeRequestRepository.save(request)

    fun findRequestById(id: Int) =
        exchangeRequestRepository
            .findById(id)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "No such request") }

    fun findRequestsToUser(): Collection<ExchangeRequestEntity> =
        exchangeRequestRepository.findRequestsToUser(userService.currentlyAuthenticatedUser.id).toList()

    fun findRequestByUser(): Collection<ExchangeRequestEntity> =
        exchangeRequestRepository.findRequestsByUser(userService.currentlyAuthenticatedUser.id).toList()

    fun declineRequest(id: Int) {
        val request = findRequestById(id)

        if(userService.currentlyAuthenticatedUser.id != request.toUserId) {
            log.debug("User with id ${userService.currentlyAuthenticatedUser.id} tried to decline request ${ObjectMapper().writeValueAsString(request)}")
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You can only decline request sent to you.")
        }

        log.debug("Request ${ObjectMapper().writeValueAsString(request)} has been declined.")
        exchangeRequestRepository.delete(request)
    }

    fun acceptRequest(id: Int) {
        val userid: Int = (SecurityContextHolder.getContext().authentication.principal as QueueUser).id

        val request =  try { findRequestById(id) } catch (e: Exception) {
            log.debug("User with id $id tried to accept not existing request")
            throw e
        }

        if(request.toUserId != userid){
            log.debug("User with id $id tried to accept request ${request.id} which was sent to user ${request.toUserId}")
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Accepting other user's requests is not allowed")
        }

        validateRequest(request)
        applyRequest(request)
    }

    private fun validateRequest(request: ExchangeRequestEntity) {
        if(request.fromDate.toLocalDate().isBefore(LocalDate.now()) || request.toDate.toLocalDate().isBefore(LocalDate.now()))
            handleInvalidRequest(request, "Request expired.")

        if(!occurrenceService.doesUserOccur(request.fromLessonId, request.fromDate, request.fromUserId))
            handleInvalidRequest(request, "Request author does not occur as specified in request")

        if(!occurrenceService.doesUserOccur(request.toLessonId, request.toDate, request.toUserId))
            handleInvalidRequest(request, "Request target does not occur as specified in request")

        log.debug("Request ${ObjectMapper().writeValueAsString(request)} passed validation.")
    }

    private fun applyRequest(request: ExchangeRequestEntity) {
        log.debug("Applying request ${request.id}")

        val fromDateChange = changeService.saveDateChange(
            DateChangeEntity(0, request.fromLessonId, request.fromDate, request.toUserId))

        val toDateChange = changeService.saveDateChange(
            DateChangeEntity(0, request.toLessonId, request.toDate, request.fromUserId))

        val exchange = request.run {
            ExchangeEntity(
                0,
                fromUserId,
                fromLessonId,
                fromDate,
                toUserId,
                toLessonId,
                toDate,
                Date.valueOf(LocalDate.now()),
                fromDateChange.id,
                toDateChange.id
            )
        }
        exchangeRepository.save(exchange)
        exchangeRequestRepository.delete(request)
    }

    fun handleInvalidRequest(request: ExchangeRequestEntity, message: String): Nothing {
        exchangeRequestRepository.delete(request)
            request.apply {
                log.debug("Exchange request with id $id was invalid. Error message: $message." + "Request: ${ObjectMapper().writeValueAsString(request)}") }

        throw ResponseStatusException(HttpStatus.CONFLICT, message)
    }

}
