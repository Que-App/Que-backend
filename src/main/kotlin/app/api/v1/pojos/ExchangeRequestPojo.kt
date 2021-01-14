package app.api.v1.pojos

import app.data.entities.ExchangeRequestEntity
import java.sql.Timestamp
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

    @field:NotNull(message = "fromIndex must be specified")
    @field:Min(0, message = "fromIndex must be positive")
    var fromIndex: Int?,

    @field:NotNull(message = "toUserId must be specified")
    @field:Min(0, message = "toUserId must be positive")
    var toUserId: Int?,

    @field:NotNull(message = "toLessonId must be specified")
    @field:Min(0, message = "toLessonId must be positive")
    var toLessonId: Int?,

    @field:NotNull(message = "toIndex must be specified")
    @field:Min(0, message = "toIndex must be positive")
    var toIndex: Int?,

    @field:NotNull(message = "Request status must not be null")
    var status: ExchangeRequestEntity.Status? = ExchangeRequestEntity.Status.PENDING,

    var resolvementTime: Timestamp?,

)