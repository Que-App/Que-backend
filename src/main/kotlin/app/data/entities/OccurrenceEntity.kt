package app.data.entities

import java.sql.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "occurrence_log")
class OccurrenceEntity(

    @Id
    @GeneratedValue
    var id: Int,

    var lessonid: Int?,

    var lessonindex: Int,

    var date: Date,

    var userid: Int?,
)