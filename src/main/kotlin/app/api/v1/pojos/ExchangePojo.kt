package app.api.v1.pojos

import app.api.v1.validation.FutureDate
import java.sql.Date
import javax.validation.constraints.Min

class ExchangePojo (
    @field:Min(0, message = "Id must not be negative")
    var id: Int?,

    @field:Min(0, message = "fromUserId must not be negative")
    var fromUserId: Int?,

    @field:Min(0, message = "fromLessonId must not be negative")
    var fromLessonId: Int?,

    @field:FutureDate("Please specify future date for fromDate")
    var fromDate: Date?,

    @field:Min(0, message = "toUserId must not be negative")
    var toUserId: Int?,

    @field:Min(0, message = "toLessonId must not be negative")
    var toLessonId: Int?,

    @field:FutureDate("Please specify future date for toDate")
    var toDate: Date?,

    @field:FutureDate("Please specify future date for acceptDate")
    var acceptDate: Date?,

    @field:Min(0, message = "fromChangeId must not be negative")
    var fromChangeId: Int?,

    @field:Min(0, message = "toChangeId must not be negative")
    var toChangeId: Int?,
)