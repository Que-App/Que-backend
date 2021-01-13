package app.data.repositories.changes

import app.data.entities.changes.DateChangeEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface DateChangeRepository : BaseChangeRepository<DateChangeEntity>, CrudRepository<DateChangeEntity, Int> {

    @Query("SELECT * FROM date_changes WHERE lesson_id = :lessonId ORDER", nativeQuery = true)
    override fun findChangesForLesson(lessonId: Int): Iterable<DateChangeEntity>

    @Query("SELECT * FROM date_changes c WHERE lesson_id = :lessonId AND creation_time > ALL (" +
            "SELECT creation_time FROM user_changes WHERE change_id != c.change_id AND lesson_index = c.lesson_index)", nativeQuery = true)
    override fun findMostRecentForIndexes(lessonId: Int): Iterable<DateChangeEntity>
}