package app.api.v1.pojos

import app.api.v1.MappingComponent
import app.data.entities.LessonEntity
import app.data.entities.OccurrenceEntity
import app.data.entities.SubjectEntity
import app.data.entities.UserEntity


fun SubjectEntity.mapToPojo() = SubjectPojo(id, name, teacher)

fun Collection<SubjectEntity>.mapToSubjectPojos() = map { it.mapToPojo() }

fun UserEntity.mapToPojo() = UserPojo(id, username)

fun Collection<UserEntity>.mapToUserPojos() = map { it.mapToPojo() }

fun OccurrenceEntity.mapToPojo(component: MappingComponent): OccurrencePojo {
    val userPojo = userId?.run { component.userService.findUserById(this) } ?: UserPojo(null, "[Removed User]")
    return OccurrencePojo(lessonId, userId, lessonIndex, date, userPojo.username)
}

fun List<OccurrenceEntity>.mapToOccurrencePojos(component: MappingComponent) = map { it.mapToPojo(component) }

fun LessonEntity.mapToPojo() = LessonPojo(id, lessonIndex, subjectId, nextDate, time, recurrenceInterval)

fun Collection<LessonEntity>.mapToLessonPojos() = map { it.mapToPojo() }

