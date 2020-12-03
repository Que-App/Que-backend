package app.data.entities.changes

import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "cancel_changes")
class CancelDateChangeEntity(

    @Id
    @GeneratedValue
    @Column(name = "change_id")
    override var id: Int,

    override var lessonId: Int?,

    var date: Date,
) : BaseChangeEntity