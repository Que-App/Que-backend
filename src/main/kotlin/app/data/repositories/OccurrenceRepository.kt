package app.data.repositories

import app.data.entities.OccurrenceEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface OccurrenceRepository : CrudRepository<OccurrenceEntity, Int> {

    @Query("SELECT * FROM occurrence_log WHERE lesson_id = :lessonId ORDER BY date DESC, time DESC LIMIT :amount", nativeQuery = true)
    fun findPreviousForLesson(lessonId: Int, amount: Int): List<OccurrenceEntity>

    @Query("SELECT * FROM occurrence_log ORDER BY date DESC, time DESC LIMIT :amount", nativeQuery = true)
    fun findPrevious(amount: Int): List<OccurrenceEntity>
}