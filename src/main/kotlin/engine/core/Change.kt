package engine.core

import app.data.entities.changes.BaseChangeEntity
import engine.util.Transaction

interface Change<T> {

    fun doesApply(transaction: Transaction<T>): Boolean

    fun apply(source: Iterator<Transaction<T>>, disposeCallback: (BaseChangeEntity) -> Unit): Iterator<Transaction<T>>

}