package app.data.repositories.changes

import app.data.entities.changes.DateChangeEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface DateChangeRepository : BaseChangeRepository<DateChangeEntity>, CrudRepository<DateChangeEntity, UUID> {

    @Query("SELECT * FROM date_changes WHERE lessonid = :lessonId", nativeQuery = true)
    override fun getChangesForLesson(lessonId: String): Iterable<DateChangeEntity>
}