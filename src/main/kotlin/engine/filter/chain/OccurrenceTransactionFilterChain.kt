package engine.filter.chain

import engine.filter.Filter
import engine.util.OccurrenceTransaction

class OccurrenceTransactionFilterChain(filters: List<Filter<OccurrenceTransaction>>)
    : BaseFilterChain<OccurrenceTransaction>(filters) {

    override fun filter(subject: OccurrenceTransaction) {
        var intercept = false

        subject.onAbort { intercept = true }

        while(iterator.hasNext() && !intercept)
            iterator.next().filter(subject)
    }
}