package engine.core

import app.data.entities.LessonEntity
import engine.util.DateTransaction
import engine.util.Transaction
import java.sql.Date
import java.time.LocalDate

class DateQueue(private val entity: LessonEntity, private val getIndex: () -> Int) : Queue<Pair<LocalDate, Int>> {

    override fun obtain(): Iterator<DateTransaction> = createIterator(entity)

    override fun peek(): Iterator<Transaction<Pair<LocalDate, Int>>> = createIterator(
        LessonEntity(
            entity.id,
            entity.lessonIndex,
            entity.subjectId,
            entity.nextDate,
            entity.pointer,
            entity.time,
            entity.recurrenceInterval,
            entity.users,
    )
    )

    private fun createIterator(entity: LessonEntity): Iterator<DateTransaction> = object : Iterator<DateTransaction> {
        override fun hasNext(): Boolean = true

        override fun next(): DateTransaction = Transaction(entity.nextDate.toLocalDate() to getIndex()) {
            entity.nextDate = Date.valueOf(entity.nextDate.toLocalDate().plusDays(entity.recurrenceInterval.toLong()))
        }
    }
}
