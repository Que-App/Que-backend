package app.api.v1.request

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class ChangePasswordRequest (

    @field:NotNull(message = "Please specify old password")
     val oldPassword: String?,

    @field:NotNull(message = "Please specify new password")
    @field:Size(min = 3, max = 30, message = "New password must be between 3 and 30 characters long")
     val newPassword: String?
)