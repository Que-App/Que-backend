package app.data.entities

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "queues")
open class QueueEntity(
    @Id
    @Type(type = "uuid-char")
    open var lessonid: UUID,

    open var pointer: Int,

    @ElementCollection
    @CollectionTable(name = "queue_users", joinColumns = [JoinColumn(name = "lessonid")])
    @Type(type = "uuid-char")
    @Column(name = "userid")
    @OrderBy("queue_order")
    open var users: MutableList<UUID>,

)