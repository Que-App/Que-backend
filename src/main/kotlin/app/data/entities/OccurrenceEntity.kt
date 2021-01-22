package app.data.entities

import java.sql.Date
import java.sql.Time
import javax.persistence.*

@Entity
@Table(name = "occurrence_log")
class OccurrenceEntity(

    @Id
    @GeneratedValue
    @Column(name = "occurrence_id")
    var id: Int,

    var lessonId: Int?,

    var lessonIndex: Int,

    var date: Date,

    var time: Time,

    var userId: Int?,
)