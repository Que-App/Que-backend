package engine.filter

@FunctionalInterface
fun interface Filter<T> {
    fun filter(subject: T): Unit
}