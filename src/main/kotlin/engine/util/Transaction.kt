package engine.util

import java.util.*

open class Transaction<T>(var data: T, onCommit: (data: T) -> Unit) {

    private val onCommitCallbacks: MutableList<(T) -> Unit> = LinkedList()

    private val onAbortCallbacks: MutableList<(T) -> Unit> = LinkedList()

    private var _aborted = false

    val aborted
        get() = _aborted



    init { onCommitCallbacks.add(onCommit) }

    fun commit(): Transaction<T> {
        if (!_aborted) onCommitCallbacks.forEach { it.invoke(data) }
        return this
    }

    fun abort(): Transaction<T> {
        onAbortCallbacks.forEach { it.invoke(data) }
        _aborted = true
        return this
    }

    fun onCommit(callback: (T) -> Unit): Transaction<T> {
        onCommitCallbacks.add(callback)
        return this
    }

    fun onAbort(callback: (T) -> Unit): Transaction<T> {
        onAbortCallbacks.add(callback)
        return this
    }
}