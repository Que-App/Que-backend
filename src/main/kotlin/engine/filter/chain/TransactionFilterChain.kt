package engine.filter.chain

import engine.filter.Filter
import engine.util.Transaction

class TransactionFilterChain<T>(val filters: List<Filter<Transaction<T>>>)
    : BaseFilterChain<Transaction<T>>(filters) {

    override fun filter(subject: Transaction<T>) {
        var intercept = false

        subject.onAbort { intercept = true }

        while (iterator.hasNext() && !intercept)
            iterator.next()
                .filter(subject)
    }
}