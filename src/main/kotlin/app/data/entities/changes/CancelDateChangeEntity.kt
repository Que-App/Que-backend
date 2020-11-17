package app.data.entities.changes

import java.sql.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "cancel_changes")
class CancelDateChangeEntity(

    @Id
    @GeneratedValue
    override var id: Int,

    override var lessonId: Int?,

    var date: Date,
) : BaseChangeEntity