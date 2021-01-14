package app.data.entities

import java.sql.Timestamp
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

    var fromIndex: Int,

    var toUserId: Int,

    var toLessonId: Int,

    var toIndex: Int,

    @Enumerated(EnumType.STRING)
    var status: Status,

    var resolvementTime: Timestamp?,

) {
    enum class Status {
        PENDING,
        ACCEPTED,
        DECLINED,
        INVALID
    }
}