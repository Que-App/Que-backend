package engine.core

import app.data.entities.QueueEntity
import engine.util.Transaction
import java.util.*

class Queue(var lessonid: UUID, var pointer: Int, var users: MutableList<UUID>) {

    constructor(e: QueueEntity) : this(e.lessonid, e.pointer, e.users)

    fun getEntity(): QueueEntity = QueueEntity(lessonid, pointer, users)

    fun get(): Transaction<UUID> = Transaction(users[pointer]) {
        pointer++
        if(pointer >= users.size) pointer = 0
    }

    fun peek(amount: Int): List<UUID> {
        val list: MutableList<UUID> = LinkedList()
        var localPointer = pointer
        repeat(amount) {
            list.add(users[localPointer])
            localPointer++
            if(localPointer >= users.size) localPointer = 0
        }
        return list
    }

}