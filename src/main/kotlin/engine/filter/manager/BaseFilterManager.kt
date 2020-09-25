package engine.filter.manager

import engine.filter.Filter
import engine.filter.chain.BaseFilterChain
import engine.util.Transaction

class BaseFilterManager<T>(chains: List<BaseFilterChain<T>>): Filter<T> {

    val iterator: Iterator<BaseFilterChain<T>> = chains.iterator()

    override fun filter(subject: T) {
        while (iterator.hasNext())
            iterator.next().filter(subject)
    }
}