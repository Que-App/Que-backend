package app.data.entities.changes

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "user_changes")
class UserChangeEntity(

    @Id
    @GeneratedValue
    @Column(name = "change_id")
    override var id: Int,

    override var lessonId: Int,

    var lessonIndex: Int,

    var userId: Int,

    var creationTime: Timestamp,

    ) : BaseChangeEntity