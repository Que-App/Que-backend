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

    var lessonId: Int?,

    var lessonIndex: Int,

    var date: Date,

    var userId: Int?,
)