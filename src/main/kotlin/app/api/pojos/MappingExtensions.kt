package app.api.pojos

import app.data.entities.OccurrenceEntity
import app.data.entities.SubjectEntity
import app.services.UserService
import org.springframework.beans.factory.annotation.Autowired

@Autowired
lateinit var userService: UserService

fun SubjectEntity.mapToPojo() = SubjectPojo(id.toString(), name, teacher)

fun List<SubjectEntity>.mapToSubjectPojos() = map { it.mapToPojo() }

fun OccurrenceEntity.mapToPojo(): OccurrencePojo {
    val userData = userService.getUserData(lessonid)
    return OccurrencePojo(lessonid, userid, date, userData.first, userData.second)
}

fun List<OccurrenceEntity>.mapToOccurrencePojos() = map { it.mapToPojo() }

