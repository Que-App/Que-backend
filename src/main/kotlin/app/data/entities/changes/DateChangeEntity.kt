package app.data.entities.changes

import java.sql.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "date_changes")
class DateChangeEntity(

    @Id
    @GeneratedValue
    override var id: Int,

    override var lessonid: Int?,

    var date: Date,

    var userid: Int?,
) : BaseChangeEntity