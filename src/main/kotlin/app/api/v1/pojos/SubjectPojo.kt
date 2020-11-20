package app.api.v1.pojos

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class SubjectPojo(

    @field:Min(0, message = "Id can not be negative")
    val id: Int?,

    @field:NotNull(message = "Name must be specified.")
    @field:Size(min = 5, max = 30, message = "Name must be between 5 and 30 characters long")
    val name: String?,

    @field:NotNull(message = "Name must be specified.")
    @field:Size(min = 5, max = 30, message = "Teacher must be between 5 and 30 characters long")
    val teacher: String?,
)