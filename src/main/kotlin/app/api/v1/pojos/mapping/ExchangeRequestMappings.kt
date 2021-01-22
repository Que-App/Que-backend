package app.api.v1.pojos.mapping

import app.api.v1.MappingComponent
import app.api.v1.pojos.ExchangeRequestPojo
import app.data.entities.ExchangeRequestEntity
import java.sql.Date

fun Collection<ExchangeRequestEntity>.mapToExchangeRequestPojos(component: MappingComponent): Collection<ExchangeRequestPojo> {
    val indexToDateMapper = component.occurrenceService.createIndexToDateMapper()


    return map {
        val fromLesson = component.lessonService.findLesson(it.fromLessonId)
        val toLesson = component.lessonService.findLesson(it.toLessonId)

        ExchangeRequestPojo(
            it.id,
            it.fromUserId,
            it.fromLessonId,
            it.fromIndex,
            it.toUserId,
            it.toLessonId,
            it.toIndex,
            it.status,
            it.resolvementTime?.time,
            component.userService.findUserById(it.fromUserId).username,
            component.userService.findUserById(it.toUserId).username,
            Date.valueOf(indexToDateMapper.mapDate(it.fromLessonId, it.fromIndex).toLocalDate()),
            Date.valueOf(indexToDateMapper.mapDate(it.toLessonId, it.toIndex).toLocalDate()),
            fromLesson.time,
            toLesson.time,
            component.subjectService.findSubject(fromLesson.subjectId).name,
            component.subjectService.findSubject(toLesson.subjectId).name,
        )
    }
}

fun ExchangeRequestPojo.mapToEntity() = ExchangeRequestEntity(
    0, fromUserId!!, fromLessonId!!, fromIndex!!, toUserId!!, toLessonId!!, toIndex!!,
    status?: ExchangeRequestEntity.Status.PENDING, null,
)

fun Collection<ExchangeRequestPojo>.mapToEntities() = map { it.mapToEntity() }

