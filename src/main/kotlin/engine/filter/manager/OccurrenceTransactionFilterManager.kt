package engine.filter.manager

import engine.filter.chain.OccurrenceTransactionFilterChain
import engine.util.OccurrenceTransaction

class OccurrenceTransactionFilterManager(chains: List<OccurrenceTransactionFilterChain>)
    : BaseFilterManager<OccurrenceTransaction>(chains) {

    override fun filter(subject: OccurrenceTransaction) {

        var aborted = false;

        subject.onAbort { aborted = true }

        while(iterator.hasNext() && !aborted)
            iterator.next().filter(subject)
    }
}