package app.data.entities

import java.sql.Date
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

    var userId: Int?,
)