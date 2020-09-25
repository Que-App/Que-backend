package engine.core

import app.data.entities.LessonEntity
import app.data.entities.QueueEntity
import engine.util.Transaction
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.sql.Date
import java.sql.Time
import java.time.LocalDate
import java.util.*
import javax.persistence.Entity

class Lesson(var id: UUID, var lessonindex: Int, var subjectid: UUID, var nextdate: Date,
             var time: Time, var recurrenceinterval: Int, var queue: QueueEntity,) {

    constructor(e: LessonEntity)
            : this(e.id, e.lessonindex, e.subjectid, e.nextdate, e.time, e.recurrenceinterval, e.queue)

    fun getEntity(): LessonEntity
            = LessonEntity(id, lessonindex, subjectid, nextdate, time, recurrenceinterval, queue)

    fun occurrencesUntill(untill: LocalDate): Int {
        var counter: Int = 1
        var date = nextdate.toLocalDate()

        while(!date.isAfter(untill.minusDays(recurrenceinterval.toLong()))) {
            counter++
            date = date.plusDays(recurrenceinterval.toLong())
        }
        return counter
    }

    fun nextDate(): Transaction<Date> = Transaction(nextdate) {
        nextdate = Date.valueOf(nextdate.toLocalDate().plusDays(recurrenceinterval.toLong()))
        lessonindex++
    }

    fun peekNextDates(): Iterator<Pair<Transaction<Date>, Lesson>> = DateIterator(nextdate, recurrenceinterval, this)

    private class DateIterator(var nextDate: Date, var recurrenceinterval: Int, val lesson: Lesson)
        : Iterator<Pair<Transaction<Date>, Lesson>> {
        override fun hasNext(): Boolean = true

        override fun next(): Pair<Transaction<Date>, Lesson> = Transaction(nextDate) {
            nextDate = Date.valueOf(nextDate.toLocalDate().plusDays(recurrenceinterval.toLong()))
        } to lesson
    }
}
