package engine.core

import app.data.entities.LessonEntity
import engine.exceptions.EmptyQueueException
import engine.util.Transaction
import engine.util.UserTransaction
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import util.copy

class UserQueue(val entity: LessonEntity, private val getIndex: () -> Int): Queue<Pair<Int, Int>> {

    companion object {
        private val log: Logger = LogManager.getLogger()
    }

    override fun obtain(): Iterator<UserTransaction> = createIterator(entity)

    override fun peek(): Iterator<UserTransaction> = createIterator(entity.copy())


    private fun createIterator(entity: LessonEntity): Iterator<UserTransaction> = object : Iterator<UserTransaction> {
        init {
            if(entity.users.isEmpty()) throw EmptyQueueException()
        }

        override fun hasNext(): Boolean = true

        //TODO: This can probably be done without code duplication. Don't want to use recursion though
        // in case something goes terribly wrong
        override fun next(): UserTransaction = try {
            Transaction(entity.users[entity.pointer] to getIndex()) {
                entity.pointer++
                if (entity.pointer >= entity.users.size) entity.pointer = 0
            }
        } catch (e: IndexOutOfBoundsException) {
            log.warn("IndexOutOfBounds exception encountered when retrieving user. Resetting pointer to 0.")
            entity.pointer = 0;
            Transaction(entity.users[entity.pointer] to getIndex()) {
                entity.pointer++
                if (entity.pointer >= entity.users.size) entity.pointer = 0
            }
        }
    }

}