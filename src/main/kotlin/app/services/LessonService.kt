package app.services

import app.data.entities.LessonEntity
import app.data.repositories.LessonRepository
import engine.core.Lesson
import engine.util.Transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.sql.Date
import java.util.*

@Service
class LessonService {

    @Autowired
    lateinit var lessonRepository: LessonRepository

    fun findAllLessons(): List<LessonEntity> = lessonRepository.findAll().toList()

    fun findLesson(id: UUID): LessonEntity = lessonRepository.findById(id).orElseThrow {
        ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found")
    }

    fun saveLesson(lessonEntity: LessonEntity) = lessonRepository.save(lessonEntity)

    fun deleteLesson(lessonEntity: LessonEntity) = lessonRepository.delete(lessonEntity)

    fun deleteLesson(id: UUID) = lessonRepository.deleteById(id)

    fun nextDate(lessonId: UUID): Pair<Transaction<Date>, Lesson> =
        lessonRepository.findById(lessonId)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found") }
            .let { Lesson(it) }
            .run { nextDate().onCommit {
                lessonRepository.save( getEntity() ) } to this
            }

    fun peekNextDates(lessonId: UUID): Iterator<Pair<Transaction<Date>, Lesson>> =
        lessonRepository.findById(lessonId)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found.") }
            .run { Lesson(this) }
            .peekNextDates()

}