package app.services

import app.data.entities.OccurrenceEntity
import app.data.repositories.OccurrenceRepository
import engine.core.IndexQueue
import engine.util.DateTransaction
import engine.util.Transaction
import engine.util.UserTransaction
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Date
import java.time.LocalDate

@Service
class OccurrenceService {

    @Autowired
    private lateinit var dateQueueService: DateQueueService

    @Autowired
    private lateinit var userQueueService: UserQueueService

    @Autowired
    private lateinit var lessonService: LessonService

    @Autowired
    private lateinit var occurrenceRepository: OccurrenceRepository


    companion object {
        val log: Logger = LogManager.getLogger()
    }

    fun findPrevious(lessonId: Int, amount: Int): List<OccurrenceEntity> = occurrenceRepository.findPrevious(lessonId, amount)

    fun getNextOccurrence(lessonId: Int): Transaction<OccurrenceEntity> {
        val lesson = lessonService.findLesson(lessonId)

        val indexTransaction = IndexQueue(lesson).obtain().next()
        val getIndex = { indexTransaction.data }

        val dateTransaction = dateQueueService.obtain(lesson, getIndex).next()
        val userTransaction = userQueueService.obtain(lesson, getIndex).next()

        val occurrence = OccurrenceEntity(
            0,
            lessonId,
            getIndex(),
            Date.valueOf(dateTransaction.data.first),
            userTransaction.data.first,
        )

        return Transaction(occurrence) {
            dateTransaction.commit()
            userTransaction.commit()
            indexTransaction.commit()

            lessonService.saveLesson(lesson)
            occurrenceRepository.save(occurrence)
        }


    }

    // TODO: We are querying the database twice on each commit, maybe I could optimize it more?
    // On the other hand, bulk commits will not happen very often, and if they are made
    // regular they will query the db twice each anyway.
    private final tailrec fun commitPast(lessonId: Int) {
        val occurrenceTransaction = getNextOccurrence(lessonId)

        if(!occurrenceTransaction.data.date.toLocalDate().isBefore(LocalDate.now())) return
        else {
            occurrenceTransaction.commit()
            commitPast(lessonId)
        }
    }

    fun peekOccurrences(lessonId: Int): Iterator<OccurrenceEntity> {
        commitPast(lessonId)

        val lesson = lessonService.findLesson(lessonId)

        val indexQueue = IndexQueue(lesson).peek()
        val getIndex = { indexQueue.next().data }

        return PeekOccurrenceTransactionIterator(
            dateQueueService.peek(lesson, getIndex),
            userQueueService.peek(lesson,getIndex),
            indexQueue,
            lessonId
        ).asSequence()
            .map {
                it.commit()
                it.data
            }
            .iterator()
    }


    private class PeekOccurrenceTransactionIterator(
        private val dateIterator: Iterator<DateTransaction>,
        private val userIterator: Iterator<UserTransaction>,
        private val indexIterator: Iterator<Transaction<Int>>,
        private val lessonId: Int,
    ) : Iterator<Transaction<OccurrenceEntity>> {

        override fun hasNext(): Boolean = true

        override fun next(): Transaction<OccurrenceEntity> {
            val dateTransaction = dateIterator.next()
            val userTransaction = userIterator.next()
            val indexTransaction = indexIterator.next()

            val occurrence = OccurrenceEntity(
                0,
                lessonId,
                indexTransaction.data,
                Date.valueOf(dateTransaction.data.first),
                userTransaction.data.first,
            )

            return Transaction(occurrence) {
                dateTransaction.commit()
                userTransaction.commit()
                indexTransaction.commit()
            }

        }
    }
}