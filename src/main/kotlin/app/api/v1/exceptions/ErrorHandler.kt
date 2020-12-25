package app.api.v1.exceptions

import app.services.exceptions.EntityNotFoundException
import app.services.exceptions.InvalidExchangeRequestException
import app.services.exceptions.UnauthorizedException
import engine.exceptions.EmptyQueueException
import org.springframework.beans.TypeMismatchException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.*
import javax.servlet.http.HttpServletRequest

//TODO: Fix this
@ControllerAdvice
class ErrorHandler : ResponseEntityExceptionHandler() {

    @Autowired
    private lateinit var request: HttpServletRequest

    private fun ApiError.createResponseEntity() = ResponseEntity<Any>(this, this.status)

    override fun handleMissingPathVariable(
        ex: MissingPathVariableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> =
        ApiError(
            status,
            "Missing path variable for ${ex.variableName}"
        ).createResponseEntity()

    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> =
        ApiError(
            status,
            "Missing parameter ${ex.parameterName}"
        ).createResponseEntity()

    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> =
        ApiError(
            status,
            "Type mismatch for ${ex.propertyName}: expected: ${ex.requiredType}"
        ).createResponseEntity()

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> =
        ApiError(
            status,
            "Message not readable: ${ex.message}"
        ).createResponseEntity()

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val subErrors: MutableList<ApiSubError> = LinkedList()

        ex.bindingResult.fieldErrors.forEach {
            subErrors.add(
                ApiSubError(
                "Invalid value: ${it.rejectedValue} - ${it.defaultMessage}",
                "Check the rejected value against error"
            )
            )
        }

        return ApiError(
            HttpStatus.BAD_REQUEST,
            "Validation failed",
            "Take look at the errors",
            subErrors = subErrors
        ).createResponseEntity()
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(ex: EntityNotFoundException): ResponseEntity<Any> =
        ApiError(
            HttpStatus.NOT_FOUND,
            ex.message ?: "Not found",
        ).createResponseEntity()

    @ExceptionHandler(EmptyQueueException::class)
    fun handleEmptyQueueException(ex: EmptyQueueException): ResponseEntity<Any> =
        ApiError(
            HttpStatus.CONFLICT,
            "There are no users in requested queue",
        "Add some users to the queue",
        ).createResponseEntity()

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException): ResponseEntity<Any> =
        ApiError(
            HttpStatus.UNAUTHORIZED,
            "Invalid username or password",
            "Retry and make sure CapsLock is disabled"
        ).createResponseEntity()

    @ExceptionHandler(DisabledException::class)
    fun handleAccountDisabled(ex: DisabledException): ResponseEntity<Any> =
        ApiError(
            HttpStatus.UNAUTHORIZED,
            "Your account has been disabled"
        ).createResponseEntity()

    @ExceptionHandler(LockedException::class)
    fun handleAccountLocked(ex: LockedException) =
        ApiError(
            HttpStatus.UNAUTHORIZED,
            "Your account has been locked"
        ).createResponseEntity()

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(ex: UnauthorizedException) =
        ApiError(
            HttpStatus.FORBIDDEN,
            "Access denied: ${ex.message}",
            "If you think this should not happen, please contact the system administrator"
        )
            .createResponseEntity()

    @ExceptionHandler(InvalidExchangeRequestException::class)
    fun handleInvalidExchangeRequest(ex: InvalidExchangeRequestException) =
        ApiError(
            HttpStatus.CONFLICT,
            "Exchange request was invalid: ${ex.message}. It was removed.",
            "If you believe the request was valid, pleas contact the system administrator as soon as possible"
        )
            .createResponseEntity()

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: java.lang.Exception) =
        ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "We are sorry, something went wrong. This error was logged and will be looked into.",
            "If this issue repeats, please contact the system administrator"
        )
            .createResponseEntity()
}