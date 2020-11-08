package app.data.entities

import java.sql.Date
import java.sql.Time
import javax.persistence.*

@Entity
@Table(name = "lessons")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
open class LessonEntity(

    @Id
    @GeneratedValue
    open var id: Int,

    open var lessonIndex: Int,

    open var subjectId: Int,

    open var nextDate: Date,

    open var pointer: Int,

    open var time: Time,

    open var recurrenceInterval: Int,

    @ElementCollection
    @CollectionTable(name = "queue_users", joinColumns = [JoinColumn(name = "lesson_id")])
    @Column(name = "user_id")
    @OrderBy("queue_order")
    open var users: MutableList<Int>,
    )
