package app.services

import engine.core.Queue
import engine.util.Transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class QueueService {

    @Autowired
    private lateinit var lessonService: LessonService

    fun peekQueue(lessonId: Int, amount: Int): List<Transaction<Int>> = lessonService.findLesson(lessonId)
        .run { Queue(this) }
        .peek(amount)
        .map { Transaction(it) {} }

    fun peekQueueIterator(lessonId: Int): Iterator<Transaction<Int>> =
        lessonService.findLesson(lessonId)
            .run { Queue(this) }
            .peekIterator()
            .run { TransactionIntWrapperIterator(this) }

    fun getFromQueue(lessonId: Int): Transaction<Int> =
        lessonService.findLesson(lessonId)
            .run { Queue(this) }
            .run { get().onCommit { lessonService.saveLesson( entity ) } }

    private class TransactionIntWrapperIterator(private val iterator: Iterator<Int>) : Iterator<Transaction<Int>> {
        override fun hasNext(): Boolean = true

        override fun next(): Transaction<Int> = Transaction(iterator.next()) {}
    }


}