package app.services

import app.data.entities.LessonEntity
import app.data.repositories.LessonRepository
import app.services.exceptions.EntityNotFoundException
import engine.core.DateQueue
import engine.util.Transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Date

@Service
class LessonService {

    @Autowired
    private lateinit var lessonRepository: LessonRepository

    fun findAllLessons(): List<LessonEntity> = lessonRepository.findAll().toList()

    fun findLesson(id: Int): LessonEntity = lessonRepository
        .findById(id)
        .orElseThrow {
        throw EntityNotFoundException("lesson")
    }

    fun saveLesson(lessonEntity: LessonEntity) = lessonRepository.save(lessonEntity)

    fun deleteLesson(lessonEntity: LessonEntity) = lessonRepository.delete(lessonEntity)

    fun deleteLesson(id: Int) = lessonRepository.deleteById(id)


}