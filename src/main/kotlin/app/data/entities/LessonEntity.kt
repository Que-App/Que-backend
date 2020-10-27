package app.data.entities

import java.sql.Date
import java.sql.Time
import javax.persistence.*

@Entity
@Table(name = "lessons")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
open class LessonEntity(

    @Id
    @Column(name = "id")
    @GeneratedValue
    open var id: Int,

    open var lessonindex: Int,

    open var subjectid: Int,

    open var nextdate: Date,

    open var pointer: Int,

    open var time: Time,

    open var recurrenceinterval: Int,

    @ElementCollection
    @CollectionTable(name = "queue_users", joinColumns = [JoinColumn(name = "lessonid")])
    @Column(name = "userid")
    @OrderBy("queue_order")
    open var users: MutableList<Int>,
    )
