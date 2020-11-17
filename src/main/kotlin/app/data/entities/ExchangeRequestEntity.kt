package app.data.entities

import java.sql.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "exchange_requests")
class ExchangeRequestEntity(

    @Id
    @GeneratedValue
    var id: Int,

    var fromUserId: Int,

    var fromLessonId: Int,

    var fromDate: Date,

    var toUserId: Int,

    var toLessonId: Int,

    var toDate: Date,
)