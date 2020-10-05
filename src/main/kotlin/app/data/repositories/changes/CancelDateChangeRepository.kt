package app.data.repositories.changes

import app.data.entities.changes.CancelDateChangeEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface CancelDateChangeRepository : BaseChangeRepository<CancelDateChangeEntity>, CrudRepository<CancelDateChangeEntity, UUID> {

    @Query("SELECT * FROM cancel_date_changes WHERE lessonid = :lessonId", nativeQuery = true)
    override fun findChangesForLesson(lessonId: String): Iterable<CancelDateChangeEntity>
}