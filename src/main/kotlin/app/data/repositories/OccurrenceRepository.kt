package app.data.repositories

import app.data.entities.OccurrenceEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface OccurrenceRepository : CrudRepository<OccurrenceEntity, UUID> {

    @Query("SELECT * FROM occurrence_log ORDER BY date DESC LIMIT :amount", nativeQuery = true)
    fun findPrevious(amount: Int): List<OccurrenceEntity>
}