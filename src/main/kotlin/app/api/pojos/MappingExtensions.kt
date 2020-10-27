package app.api.pojos

import app.data.entities.LessonEntity
import app.data.entities.OccurrenceEntity
import app.data.entities.SubjectEntity
import app.data.entities.UserEntity
import app.services.UserService
import org.springframework.beans.factory.annotation.Autowired

@Autowired
lateinit var userService: UserService

fun SubjectEntity.mapToPojo() = SubjectPojo(id, name, teacher)

fun Collection<SubjectEntity>.mapToSubjectPojos() = map { it.mapToPojo() }

fun UserEntity.mapToPojo() = UserPojo(id, username)

fun Collection<UserEntity>.mapToUserPojos() = map { it.mapToPojo() }

fun OccurrenceEntity.mapToPojo(): OccurrencePojo {
    val userPojo = userid?.run { userService.findUser(this) } ?: UserPojo(null, "[Removed User]")
    return OccurrencePojo(lessonid, userid, lessonindex, date, userPojo.username)
}

fun List<OccurrenceEntity>.mapToOccurrencePojos() = map { it.mapToPojo() }

fun LessonEntity.mapToPojo() = LessonPojo(id, lessonindex, subjectid, nextdate, time, recurrenceinterval)

fun Collection<LessonEntity>.mapToLessonPojos() = map { it.mapToPojo() }

