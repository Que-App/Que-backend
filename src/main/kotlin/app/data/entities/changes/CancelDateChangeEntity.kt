package app.data.entities.changes

import org.hibernate.annotations.Type
import java.sql.Date
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "cancel_changes")
class CancelDateChangeEntity(

    @Id
    @GeneratedValue(generator = "UUID")
    @Type(type = "uuid-char")
    override var id: UUID,

    @Type(type = "uuid-char")
    override var lessonid: UUID,

    var date: Date,
) : BaseChangeEntity