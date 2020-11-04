package util

import org.springframework.http.ResponseEntity
import java.util.*

fun <T> T?.optional(): Optional<T> = Optional.ofNullable(this)

fun Any?.ok(): ResponseEntity<Any?> = ResponseEntity.ok().build()

fun <T> Any?.ok(body: T?): ResponseEntity<T?> = ResponseEntity.ok().body(body)

fun Any?.badRequest(): ResponseEntity<Any?> = ResponseEntity.badRequest().build()

fun <T> Any?.badRequest(body: T?): ResponseEntity<T?> = ResponseEntity.badRequest().body(body)