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
import java.time.LocalDateTime

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

    fun findPrevious(amount: Int) = occurrenceRepository.findPrevious(amount)

    fun findPreviousForLesson(lessonId: Int, amount: Int): List<OccurrenceEntity> = occurrenceRepository.findPreviousForLesson(lessonId, amount)

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

    private final tailrec fun commitPast(lessonId: Int) {
        val occurrenceTransaction = getNextOccurrence(lessonId)
        val lesson = lessonService.findLesson(lessonId)

        if(!LocalDateTime.of(occurrenceTransaction.data.date.toLocalDate(), lesson.time.toLocalTime()).isBefore(LocalDateTime.now())) return

        occurrenceTransaction.commit()
        commitPast(lessonId)
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

    fun doesUserOccur(lessonId: Int, index: Int, userId: Int): Boolean {
        val lesson = lessonService.findLesson(lessonId)

        val indexQueue = IndexQueue(lesson).peek()
        var currentIndexTransaction = indexQueue.next()

        if(index < currentIndexTransaction.data) return false;

        val userQueue = userQueueService.peek(lesson) { currentIndexTransaction.data }

        while(currentIndexTransaction.data < index) {
            currentIndexTransaction.commit()
            currentIndexTransaction = indexQueue.next()
            userQueue.next().commit()
        }

        return userQueue.next().data.first == userId
    }

    fun peekNextDates(lessonId: Int): Iterator<DateTransaction> {
        val lesson = lessonService.findLesson(lessonId)
        val indexQueue = IndexQueue(lesson).peek()
        val dateQueue = dateQueueService.peek(lesson) { indexQueue.next().data }

        return PeekDateTransactionIterator(
            indexQueue,
            dateQueue,
        )
    }

    fun createIndexToDateMapper(): IndexToDateMapper = CachingIndexToDateMapper()


    private class PeekDateTransactionIterator(
        private val indexIterator: Iterator<Transaction<Int>>,
        private val dateIterator: Iterator<DateTransaction>,
    ) : Iterator<DateTransaction> {

        override fun hasNext(): Boolean = true

        override fun next(): DateTransaction {
            val dateTransaction = dateIterator.next()
            val indexTransaction = indexIterator.next()

            return Transaction(dateTransaction.data) {
                dateTransaction.commit()
                indexTransaction.commit()
            }
        }
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

    interface IndexToDateMapper {

        fun mapDate(lessonId: Int, index: Int): LocalDate

    }

    private inner class CachingIndexToDateMapper : IndexToDateMapper {

        private val cache: HashMap<Int, Pair<Iterator<DateTransaction>, MutableList<LocalDate>>> = HashMap()

        override fun mapDate(lessonId: Int, index: Int): LocalDate {

            val cached = cache.computeIfAbsent(lessonId) { peekNextDates(lessonId) to ArrayList() }

            while(cached.second.size < index) {
                val transaction = cached.first.next()
                transaction.commit()
                cached.second.add(transaction.data.first)
            }

            return cached.second[index-1]

        }
    }
}