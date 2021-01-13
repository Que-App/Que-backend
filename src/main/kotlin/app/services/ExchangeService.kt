package app.services

import app.data.entities.ExchangeEntity
import app.data.entities.ExchangeRequestEntity
import app.data.entities.changes.DateChangeEntity
import app.data.repositories.ExchangeRequestRepository
import app.data.repositories.ExchangesRepository
import app.services.exceptions.EntityNotFoundException
import app.services.exceptions.InvalidExchangeRequestException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.sql.Date
import java.time.LocalDate

@Service
class ExchangeService {

//    companion object {
//        private val log: Logger = LogManager.getLogger()
//    }
//
//    @Autowired
//    private lateinit var exchangeRequestRepository: ExchangeRequestRepository
//
//    @Autowired
//    private lateinit var exchangeRepository: ExchangesRepository
//
//    @Autowired
//    private lateinit var changeService: ChangeService
//
//    @Autowired
//    private lateinit var occurrenceService: OccurrenceService
//
//    @Autowired
//    private lateinit var userService: UserService
//
//
//
//    fun findAllExchanges() = exchangeRepository.findAll()
//
//    fun findExchangesForUser(id: Int) = exchangeRepository.findChangesForUser(id)
//
//    fun saveRequest(request: ExchangeRequestEntity) =
//        exchangeRequestRepository.save(request)
//
//    fun findRequestById(id: Int) =
//        exchangeRequestRepository
//            .findById(id)
//            .orElseThrow { throw EntityNotFoundException("exchange request") }
//
//    fun findRequestsToUser(): Collection<ExchangeRequestEntity> =
//        exchangeRequestRepository.findRequestsToUser(userService.currentlyAuthenticatedUserDetails.id).toList()
//
//    fun findRequestByUser(): Collection<ExchangeRequestEntity> =
//        exchangeRequestRepository.findRequestsByUser(userService.currentlyAuthenticatedUserDetails.id).toList()
//
//    fun declineRequest(id: Int): Unit = declineRequest(findRequestById(id))
//
//    @PreAuthorize("request.toUserId == userService.currentlyAuthenticatedUser.id")
//    fun declineRequest(request: ExchangeRequestEntity): Unit {
//        log.debug("Request ${ObjectMapper().writeValueAsString(request)} has been declined.")
//
//        request.status = ExchangeRequestEntity.Status.DECLINED
//        request.resolvementDate = Date(java.util.Date().time)
//        saveRequest(request)
//    }
//
//    fun acceptRequest(id: Int): Unit = acceptRequest(findRequestById(id))
//
//    @PreAuthorize("request.toUserId == userService.currentlyAuthenticatedUser.id")
//    fun acceptRequest(request: ExchangeRequestEntity): Unit {
//        validateRequest(request)
//        applyRequest(request)
//    }
//
//    private fun validateRequest(request: ExchangeRequestEntity) {
//
//        if(request.status != ExchangeRequestEntity.Status.PENDING)
//            handleInvalidRequest(request, "Request had status ${request.status}")
//
//        if(request.fromDate.toLocalDate().isBefore(LocalDate.now()) || request.toDate.toLocalDate().isBefore(LocalDate.now()))
//            handleInvalidRequest(request, "Request expired")
//
//        if(!occurrenceService.doesUserOccur(request.fromLessonId, request.fromDate, request.fromUserId))
//            handleInvalidRequest(request, "Request author does not occur as specified in request")
//
//        if(!occurrenceService.doesUserOccur(request.toLessonId, request.toDate, request.toUserId))
//            handleInvalidRequest(request, "Request target does not occur as specified in request")
//
//        log.debug("Request ${ObjectMapper().writeValueAsString(request)} passed validation")
//    }
//
//    private fun applyRequest(request: ExchangeRequestEntity) {
//        log.debug("Applying request ${request.id}")
//
//        val fromDateChange = changeService.saveDateChange(
//            DateChangeEntity(0, request.fromLessonId, request.fromDate, request.toUserId))
//
//        val toDateChange = changeService.saveDateChange(
//            DateChangeEntity(0, request.toLessonId, request.toDate, request.fromUserId))
//
//        val exchange = ExchangeEntity(
//            0,
//            fromDateChange.id,
//            toDateChange.id,
//            request.id
//        )
//
//        exchangeRepository.save(exchange)
//
//        request.status = ExchangeRequestEntity.Status.ACCEPTED
//        request.resolvementDate = Date(java.util.Date().time)
//        saveRequest(request)
//    }
//
//    @PreAuthorize("request.toUserId == userService.currentlyAuthenticatedUser.id")
//    fun handleInvalidRequest(request: ExchangeRequestEntity, message: String): Nothing {
//            request.apply {
//                log.debug("Exchange request with id $id was invalid. Error message: $message." + "Request: ${ObjectMapper().writeValueAsString(request)}")
//            }
//
//        request.status = ExchangeRequestEntity.Status.INVALID
//        request.resolvementDate = Date(java.util.Date().time)
//        saveRequest(request)
//
//        throw InvalidExchangeRequestException(message)
//    }

}
