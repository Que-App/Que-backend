package app.data.repositories.changes

import app.data.entities.changes.BaseChangeEntity

interface BaseChangeRepository<T : BaseChangeEntity> {

    fun findChangesForLesson(lessonId: String): Iterable<T>
}