package util

import app.data.entities.LessonEntity
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.ResponseEntity
import java.sql.Date
import java.sql.Time
import java.time.LocalDateTime
import java.util.*

fun <T> T?.optional(): Optional<T> = Optional.ofNullable(this)

fun Any?.ok(): ResponseEntity<Any?> = ResponseEntity.ok().build()

fun <T> Any?.ok(body: T?): ResponseEntity<T?> = ResponseEntity.ok().body(body)

fun Any?.badRequest(): ResponseEntity<Any?> = ResponseEntity.badRequest().build()

fun <T> Any?.badRequest(body: T?): ResponseEntity<T?> = ResponseEntity.badRequest().body(body)

fun <T> T.repeatApply(amount: Int, action: T.(Int) -> Unit): T {
    repeat(amount) { this.action(it); }
    return this
}

private val objectWriter = ObjectMapper().writer()
fun <T> T?.json() = objectWriter.writeValueAsString(this)

fun localDateTime(date: Date, time: Time) = LocalDateTime.of(date.toLocalDate(), time.toLocalTime())

fun LessonEntity.copy() = LessonEntity(id, lessonIndex, subjectId, nextDate, pointer, time, recurrenceInterval, users)
