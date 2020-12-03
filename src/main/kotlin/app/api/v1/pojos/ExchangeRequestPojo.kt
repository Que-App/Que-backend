package app.api.v1.pojos

import app.api.v1.validation.FutureDate
import java.sql.Date
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

class ExchangeRequestPojo(

    @field:Min(0, message = "Id must be positive")
    var id: Int?,

    @field:NotNull(message = "fromUserId must be specified")
    @field:Min(0, message = "fromUserId must be positive")
    var fromUserId: Int?,

    @field:NotNull(message = "fromLessonId must be specified")
    @field:Min(0, message = "fromLessonId must be positive")
    var fromLessonId: Int?,

    @field:FutureDate
    var fromDate: Date?,

    @field:NotNull(message = "toUserId must be specified")
    @field:Min(0, message = "toUserId must be positive")
    var toUserId: Int?,

    @field:NotNull(message = "toLessonId must be specified")
    @field:Min(0, message = "toLessonId must be positive")
    var toLessonId: Int?,

    @field:FutureDate
    var toDate: Date?,
)