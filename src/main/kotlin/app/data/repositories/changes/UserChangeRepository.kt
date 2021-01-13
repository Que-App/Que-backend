package app.data.repositories.changes

import app.data.entities.changes.UserChangeEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface UserChangeRepository : BaseChangeRepository<UserChangeEntity>, CrudRepository<UserChangeEntity, Int> {

    @Query("SELECT * FROM user_changes WHERE lesson_id = :lessonId", nativeQuery = true)
    override fun findChangesForLesson(lessonId: Int): Iterable<UserChangeEntity>

    @Query("SELECT * FROM user_changes c WHERE lesson_id = :lessonId AND creation_time > ALL (" +
            "SELECT creation_time FROM user_changes WHERE change_id != c.change_id AND lesson_index = c.lesson_index)", nativeQuery = true)
    override fun findMostRecentForIndexes(lessonId: Int): Iterable<UserChangeEntity>
}