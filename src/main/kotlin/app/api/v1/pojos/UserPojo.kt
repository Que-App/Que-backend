package app.api.v1.pojos

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class UserPojo(

    @field:Min(0, message = "Id must not be negative")
    val id: Int?,

    @field:NotBlank(message = "Username must be specified")
    @field:Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters long.")
    val username: String?
)