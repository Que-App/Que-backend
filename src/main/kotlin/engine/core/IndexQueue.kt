package engine.core

import app.data.entities.LessonEntity
import engine.util.Transaction

class IndexQueue(private val entity: LessonEntity) : Queue<Int> {

    override fun obtain(): Iterator<Transaction<Int>> = object : Iterator<Transaction<Int>> {
        override fun hasNext(): Boolean = true

        override fun next(): Transaction<Int> = Transaction(entity.lessonIndex) { entity.lessonIndex++ }
    }

    override fun peek(): Iterator<Transaction<Int>> = IndexIterator(entity.lessonIndex)

    private class IndexIterator(private var index: Int) : Iterator<Transaction<Int>> {
        override fun hasNext(): Boolean = true

        override fun next(): Transaction<Int> = Transaction(index) { index++ }
    }
}