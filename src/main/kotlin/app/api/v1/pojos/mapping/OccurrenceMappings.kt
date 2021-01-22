package app.api.v1.pojos.mapping

import app.api.v1.MappingComponent
import app.api.v1.pojos.OccurrencePojo
import app.api.v1.pojos.UserPojo
import app.data.entities.OccurrenceEntity

fun OccurrenceEntity.mapToPojo(component: MappingComponent): OccurrencePojo {
    val userPojo = userId?.run { component.userService.findUserById(this).mapToPojo() } ?: UserPojo(null, "[Removed User]")
    return OccurrencePojo(lessonId, userId, lessonIndex, date, time, userPojo.username)
}

fun List<OccurrenceEntity>.mapToOccurrencePojos(component: MappingComponent) = map { it.mapToPojo(component) }