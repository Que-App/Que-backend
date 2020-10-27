package engine.util

import app.data.entities.OccurrenceEntity
import java.sql.Date

class OccurrenceTransaction( lessonId: Int,
                             lessonindex: Int,
                            val dateTransaction: Transaction<Date>,
                            val userTransaction: Transaction<Int>)
    : Transaction<OccurrenceEntity>(
    OccurrenceEntity(
        0,
        lessonId,
        lessonindex,
        dateTransaction.data,
        userTransaction.data,

    ), { dateTransaction.commit(); userTransaction.commit()} )