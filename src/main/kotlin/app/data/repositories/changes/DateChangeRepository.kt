package app.data.repositories.changes

import app.data.entities.changes.DateChangeEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface DateChangeRepository : BaseChangeRepository<DateChangeEntity>, CrudRepository<DateChangeEntity, Int> {

    @Query("SELECT * FROM date_changes WHERE lesson_id = :lessonId", nativeQuery = true)
    override fun findChangesForLesson(lessonId: Int): Iterable<DateChangeEntity>
}