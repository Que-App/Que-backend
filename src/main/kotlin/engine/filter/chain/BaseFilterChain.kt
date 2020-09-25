package engine.filter.chain

import engine.filter.Filter
import engine.util.Transaction

open class BaseFilterChain<T>(filters: List<Filter<T>>) : Filter<T> {

    val iterator: Iterator<Filter<T>> = filters.iterator()

    override fun filter(subject: T) {
        while (iterator.hasNext())
            iterator.next().filter(subject)
    }
}