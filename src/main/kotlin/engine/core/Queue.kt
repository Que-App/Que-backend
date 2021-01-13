package engine.core

import engine.util.Transaction

interface Queue<T> {

    fun obtain(): Iterator<Transaction<T>>

    fun peek(): Iterator<Transaction<T>>

}