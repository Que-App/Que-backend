package app.services.models

import app.data.entities.ExchangeRequestEntity
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDateTime
import javax.persistence.*

class ExchangeRequestCreatedEvent(

    var id: Int,

    var fromUserId: Int,

    var fromLessonId: Int,

    var fromIndex: Int,

    var toUserId: Int,

    var toLessonId: Int,

    var toIndex: Int,

    var time: LocalDateTime,

    ) {

}