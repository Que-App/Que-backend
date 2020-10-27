package app.data.repositories

import app.data.entities.OccurrenceEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface OccurrenceRepository : CrudRepository<OccurrenceEntity, Int> {

    @Query("SELECT * FROM occurrence_log where lessonid = :lessonid ORDER BY date DESC LIMIT :amount", nativeQuery = true)
    fun findPrevious(lessonid: Int, amount: Int): List<OccurrenceEntity>
}