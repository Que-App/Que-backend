package app.data.entities.changes

import java.sql.Date
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

    var creationTime: Date,
) : BaseChangeEntity