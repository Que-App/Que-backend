package app.data.entities

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "subjects")
class SubjectEntity(

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id")
    @Type(type = "uuid-char")
    var id: UUID,

    var name: String,

    var teacher: String,

    @OneToMany(targetEntity = LessonEntity::class, mappedBy = "subjectid")
    var lessonEntities: MutableList<LessonEntity>
)