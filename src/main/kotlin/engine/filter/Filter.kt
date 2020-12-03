package engine.filter

@FunctionalInterface
fun interface Filter<in T> {
    fun filter(subject: T): Unit
}