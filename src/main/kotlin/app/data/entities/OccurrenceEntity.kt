package app.data.entities

import org.hibernate.annotations.Type
import java.sql.Date
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "occurrence_log")
class OccurrenceEntity(

    @Id
    @GeneratedValue(generator = "UUID")
    @Type(type = "uuid-char")
    var id: UUID,

    @Type(type = "uuid-char")
    var lessonid: UUID,

    var lessonindex: Int,

    var date: Date,

    @Type(type = "uuid-char")
    var userid: UUID,
)