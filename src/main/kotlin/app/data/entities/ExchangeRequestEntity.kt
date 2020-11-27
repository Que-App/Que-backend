package app.data.entities

import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "exchange_requests")
class ExchangeRequestEntity(

    @Id
    @GeneratedValue
    @Column(name = "request_id")
    var id: Int,

    var fromUserId: Int,

    var fromLessonId: Int,

    var fromDate: Date,

    var toUserId: Int,

    var toLessonId: Int,

    var toDate: Date,
)