package app.api.v1.exceptions

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.util.*

class ApiError(

    val status: HttpStatus,

    val message: String,

    val suggestedAction: String = DEFAULT_SUGGESTED_ACTION_MESSAGE,

    val subErrors: List<ApiSubError> = LinkedList(),

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    val timestamp: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        const val DEFAULT_SUGGESTED_ACTION_MESSAGE = "Please show this error to system administrator"
    }
}