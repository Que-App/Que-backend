package app.api.v1.pojos

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class AuthorityPojo (

    @field:Min(3, message = "Id can not be negative")
    var id: Int,

    @field:NotNull(message = "Value must be specified")
    @Size(min = 3, max = 30, message = "Value size must be between 3 and 30 characters long")
    var value: String?,
)