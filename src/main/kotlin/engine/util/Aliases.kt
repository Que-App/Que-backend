package engine.util

import java.time.LocalDateTime

typealias DateTransaction = Transaction<Pair<LocalDateTime, Int>>

typealias UserTransaction = Transaction<Pair<Int, Int>>