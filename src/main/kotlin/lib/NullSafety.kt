package lib

import java.util.*

fun <T> T?.ifNotNull(callback:(T) -> Unit): T? {
    if (this != null) callback.invoke(this)
    return this
}

fun <T> T?.optional(): Optional<T> = Optional.ofNullable(this)