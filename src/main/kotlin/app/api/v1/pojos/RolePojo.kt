package app.api.v1.pojos

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class RolePojo (

    @field:Min(0, message = "Id can not be negative")
    var id: Int,

    @field:NotNull(message = "Please specify the role name")
    @field:Size(min = 3, max = 30, message = "Role name must be between 3 and 30 characters long")
    var name: String?,

)