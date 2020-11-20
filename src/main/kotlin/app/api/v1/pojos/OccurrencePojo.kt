package app.api.v1.pojos

import java.sql.Date
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

class OccurrencePojo(
    @field:Min(0, message = "Lesson id cannot be negative")
    val lessonid: Int?,

    @field:Min(0, message = "User id cannot be negative")
    val userid: Int?,

    @field:Min(0, message = "Lesson index cannot be negative")
    val lessonindex: Int?,

    @field:NotNull(message = "Date must be specified")
    val date: Date?,

    @field:NotNull(message = "Username must be specified")
    val username: String?,

)

