package app.data.entities.changes

import java.util.*

interface BaseChangeEntity {
    var id: UUID

    var lessonid: UUID
}