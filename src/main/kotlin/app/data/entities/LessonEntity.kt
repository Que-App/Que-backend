package app.data.entities

import org.hibernate.annotations.Type
import java.sql.Date
import java.sql.Time
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "lessons")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
open class LessonEntity(
    @Id
    @GeneratedValue(generator = "UUID")
    @Type(type = "uuid-char")
    @Column(name = "id")
    open var id: UUID,

    open var lessonindex: Int,

    @Type(type = "uuid-char")
    open var subjectid: UUID,

    open var nextdate: Date,

    open var time: Time,

    open var recurrenceinterval: Int,

    @OneToOne(targetEntity = QueueEntity::class)
    @JoinColumn(name = "id", referencedColumnName = "lessonid")
    open var queue: QueueEntity,

    )
