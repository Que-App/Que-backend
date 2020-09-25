package engine.util

import app.data.entities.OccurrenceEntity
import engine.util.Transaction
import java.sql.Date
import java.util.*

class OccurrenceTransaction( lessonId: UUID,
                             lessonindex: Int,
                            val dateTransaction: Transaction<Date>,
                            val userTransaction: Transaction<UUID>)
    : Transaction<OccurrenceEntity>(
    OccurrenceEntity(
        UUID.randomUUID(),
        lessonId,
        lessonindex,
        dateTransaction.data,
        userTransaction.data), { dateTransaction.commit(); userTransaction.commit() })