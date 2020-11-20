package app.api.v1.request

import javax.validation.constraints.NotNull

class LoginRequest(

    @field:NotNull(message = "Please specify username")
    val username: String?,

    @field:NotNull(message = "Please specify password")
    val password: String?
)