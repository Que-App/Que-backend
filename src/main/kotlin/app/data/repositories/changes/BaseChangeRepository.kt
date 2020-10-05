package app.data.repositories.changes

import app.data.entities.changes.BaseChangeEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface BaseChangeRepository<T : BaseChangeEntity> {

    fun findChangesForLesson(lessonId: String): Iterable<T>
}