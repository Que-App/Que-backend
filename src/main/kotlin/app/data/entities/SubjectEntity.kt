package app.data.entities

import javax.persistence.*

@Entity
@Table(name = "subjects")
class SubjectEntity(

    @Id
    @GeneratedValue
    @Column(name = "subject_id")
    var id: Int,

    var name: String,

    var teacher: String,

    @OneToMany(targetEntity = LessonEntity::class, mappedBy = "subjectId")
    var lessonEntities: MutableList<LessonEntity>
)