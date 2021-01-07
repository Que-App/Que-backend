package app.api.v1.pojos

import javax.validation.constraints.Min

class ExchangePojo (
    @field:Min(0, message = "Id must not be negative")
    var id: Int?,

    @field:Min(0, message = "fromChangeId must not be negative")
    var fromChangeId: Int?,

    @field:Min(0, message = "toChangeId must not be negative")
    var toChangeId: Int?,

    @field:Min(0, message = "Id must not be negateive")
    var requestId: Int?
)