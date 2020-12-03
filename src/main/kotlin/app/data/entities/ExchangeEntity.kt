package app.data.entities

import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "exchanges")
class ExchangeEntity(
    @Id
    @GeneratedValue
    @Column(name = "exchange_id")
    var id: Int,

    var fromUserId: Int?,

    var fromLessonId: Int?,

    var fromDate: Date,

    var toUserId: Int?,

    var toLessonId: Int?,

    var toDate: Date,

    var acceptDate: Date,

    var fromChangeId: Int,

    var toChangeId: Int,
)