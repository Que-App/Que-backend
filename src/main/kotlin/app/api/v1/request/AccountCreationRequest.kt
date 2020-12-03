package app.api.v1.request

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class AccountCreationRequest (

    @field:NotNull(message = "Please specify username")
    @field:Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters long")
    val username: String?,

    @field:NotNull(message = "Please specify password")
    @field:Size(min = 3, max = 30, message = "Password must be between 3 and 30 characters long")
    val password: String?
)