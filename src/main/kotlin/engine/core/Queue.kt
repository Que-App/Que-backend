package engine.core

import app.data.entities.LessonEntity
import engine.util.Transaction
import java.util.*

class Queue(val entity: LessonEntity) {

    fun get(): Transaction<Int> = Transaction(entity.users[entity.pointer]) {
        entity.pointer++
        if(entity.pointer >= entity.users.size) entity.pointer = 0
    }

    fun peek(amount: Int): List<Int> {
        val list: MutableList<Int> = LinkedList()
        var pointer = entity.pointer
        repeat(amount) {
            list.add(entity.users[pointer])
            pointer++
            if(pointer >= entity.users.size) pointer = 0
        }
        return list
    }

    fun peekIterator(): Iterator<Int> = PeekIterator(entity.pointer, entity.users)

    private class PeekIterator(var pointer: Int, val users: List<Int>) : Iterator<Int> {
        override fun hasNext(): Boolean = true

        override fun next(): Int {
            val user: Int = users[pointer]

            pointer++
            if(pointer >= users.size) pointer = 0

            return user;
        }
    }

}