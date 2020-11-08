package engine.core

import app.data.entities.LessonEntity
import engine.util.Transaction
import java.sql.Date
import java.time.LocalDate

class Lesson(val entity: LessonEntity) {

    fun occurrencesUntill(untill: LocalDate): Int {
        var counter: Int = 1
        var date = entity.nextDate.toLocalDate()

        while(!date.isAfter(untill.minusDays(entity.recurrenceInterval.toLong()))) {
            counter++
            date = date.plusDays(entity.recurrenceInterval.toLong())
        }
        return counter
    }

    fun nextDate(): Transaction<Date> = Transaction(entity.nextDate) {
        entity.nextDate = Date.valueOf(entity.nextDate.toLocalDate().plusDays(entity.recurrenceInterval.toLong()))
        entity.lessonIndex++
    }

    fun peekNextDates(): Iterator<Pair<Transaction<Date>, Lesson>> = DateIterator(entity.nextDate, entity.recurrenceInterval, this)

    private class DateIterator(var nextDate: Date, var recurrenceinterval: Int, val lesson: Lesson)
        : Iterator<Pair<Transaction<Date>, Lesson>> {
        override fun hasNext(): Boolean = true

        override fun next(): Pair<Transaction<Date>, Lesson> = Transaction(nextDate) {
            nextDate = Date.valueOf(nextDate.toLocalDate().plusDays(recurrenceinterval.toLong()))
        } to lesson
    }
}
