package app.api.v1.pojos.mapping

import app.api.v1.pojos.LessonPojo
import app.data.entities.LessonEntity
import java.util.*

fun LessonEntity.mapToPojo() = LessonPojo(id, lessonIndex, subjectId, nextDate, time, recurrenceInterval)

fun Collection<LessonEntity>.mapToLessonPojos() = map { it.mapToPojo() }

fun LessonPojo.mapToEntity() = LessonEntity(0, 0, subjectId!!, nextDate!!, 0, time!!, recurrenceInterval!!, LinkedList() )