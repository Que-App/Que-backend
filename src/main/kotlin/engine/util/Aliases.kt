package engine.util

import java.time.LocalDate

typealias DateTransaction = Transaction<Pair<LocalDate, Int>>

typealias UserTransaction = Transaction<Pair<Int, Int>>