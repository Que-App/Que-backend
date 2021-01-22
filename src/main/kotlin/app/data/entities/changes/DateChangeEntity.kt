package app.data.entities.changes

import java.sql.Date
import java.sql.Time
import javax.persistence.*

@Entity
@Table(name = "date_changes")
class DateChangeEntity(

    @Id
    @GeneratedValue
    @Column(name = "change_id")
    override var id: Int,

    override var lessonId: Int,

    var lessonIndex: Int,

    var date: Date,

    var time: Time,

    var creationTime: Date,
) : BaseChangeEntity