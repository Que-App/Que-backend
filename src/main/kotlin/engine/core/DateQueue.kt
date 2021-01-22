package engine.core

import app.data.entities.LessonEntity
import engine.util.DateTransaction
import engine.util.Transaction
import util.copy
import util.localDateTime
import java.sql.Date
import java.time.LocalDateTime

class DateQueue(private val entity: LessonEntity, private val getIndex: () -> Int) : Queue<Pair<LocalDateTime, Int>> {

    override fun obtain(): Iterator<DateTransaction> = createIterator(entity)

    override fun peek(): Iterator<DateTransaction> = createIterator(entity.copy())

    private fun createIterator(entity: LessonEntity): Iterator<DateTransaction> = object : Iterator<DateTransaction> {
        override fun hasNext(): Boolean = true

        override fun next(): DateTransaction = Transaction(localDateTime(entity.nextDate, entity.time) to getIndex()) {
            entity.nextDate = Date.valueOf(entity.nextDate.toLocalDate().plusDays(entity.recurrenceInterval.toLong()))
        }
    }
}
