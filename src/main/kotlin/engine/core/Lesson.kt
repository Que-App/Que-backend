package engine.core

import app.data.entities.LessonEntity
import engine.util.Transaction
import java.sql.Date
import java.time.LocalDate

class Lesson(val entity: LessonEntity) {

    fun occurrencesUntill(untill: LocalDate): Int {
        var counter: Int = 1
        var date = entity.nextdate.toLocalDate()

        while(!date.isAfter(untill.minusDays(entity.recurrenceinterval.toLong()))) {
            counter++
            date = date.plusDays(entity.recurrenceinterval.toLong())
        }
        return counter
    }

    fun nextDate(): Transaction<Date> = Transaction(entity.nextdate) {
        entity.nextdate = Date.valueOf(entity.nextdate.toLocalDate().plusDays(entity.recurrenceinterval.toLong()))
        entity.lessonindex++
    }

    fun peekNextDates(): Iterator<Pair<Transaction<Date>, Lesson>> = DateIterator(entity.nextdate, entity.recurrenceinterval, this)

    private class DateIterator(var nextDate: Date, var recurrenceinterval: Int, val lesson: Lesson)
        : Iterator<Pair<Transaction<Date>, Lesson>> {
        override fun hasNext(): Boolean = true

        override fun next(): Pair<Transaction<Date>, Lesson> = Transaction(nextDate) {
            nextDate = Date.valueOf(nextDate.toLocalDate().plusDays(recurrenceinterval.toLong()))
        } to lesson
    }
}
