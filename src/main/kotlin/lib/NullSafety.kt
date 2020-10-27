package lib

import java.util.*

fun <T> T?.optional(): Optional<T> = Optional.ofNullable(this)

fun Any?.returnUnit() = Unit