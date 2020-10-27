package app.data.entities

import javax.persistence.*

@Entity
@Table(name = "subjects")
class SubjectEntity(

    @Id
    @Column(name = "id")
    @GeneratedValue
    var id: Int,

    var name: String,

    var teacher: String,

    @OneToMany(targetEntity = LessonEntity::class, mappedBy = "subjectid")
    var lessonEntities: MutableList<LessonEntity>
)