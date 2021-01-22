package engine.core

import app.data.entities.LessonEntity
import engine.exceptions.EmptyQueueException
import engine.util.Transaction
import engine.util.UserTransaction
import util.copy

class UserQueue(val entity: LessonEntity, private val getIndex: () -> Int): Queue<Pair<Int, Int>> {

    override fun obtain(): Iterator<UserTransaction> = createIterator(entity)

    override fun peek(): Iterator<UserTransaction> = createIterator(entity.copy())


    private fun createIterator(entity: LessonEntity): Iterator<UserTransaction> = object : Iterator<UserTransaction> {
        init {
            if(entity.users.isEmpty()) throw EmptyQueueException()
        }

        override fun hasNext(): Boolean = true

        override fun next(): UserTransaction = Transaction(entity.users[entity.pointer] to getIndex()) {
            entity.pointer++
            if(entity.pointer >= entity.users.size) entity.pointer = 0
        }
    }

}