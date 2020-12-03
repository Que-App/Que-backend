package app.data.repositories.changes

import app.data.entities.changes.CancelDateChangeEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface CancelDateChangeRepository : BaseChangeRepository<CancelDateChangeEntity>, CrudRepository<CancelDateChangeEntity, Int> {

    @Query("SELECT * FROM cancel_date_changes WHERE lesson_id = :lessonId", nativeQuery = true)
    override fun findChangesForLesson(lessonId: Int): Iterable<CancelDateChangeEntity>
}