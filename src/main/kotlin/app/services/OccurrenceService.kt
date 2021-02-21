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
import util.copy
import util.json
import java.sql.Date
import java.sql.Time
import java.time.LocalDateTime

/* Note that every function that assembles an occurrence sequence does so on a copy of the lesson entity
* regardless of whether or not is it a peek or an obtain, so as to commits (changes to entities) done in one stream,
* do not affect the state of the entity used by another stream, which might happen because Spring cache returns the
* reference to the same object instead of a new instance with same data. An example of that might be a UserChange that
* commits the current user transaction in order to replace it with the new one in the commitPast function, that is
* invoked from within the peekOccurrences function. */

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
        private val log: Logger = LogManager.getLogger()
    }

    fun findPrevious(amount: Int) = occurrenceRepository.findPrevious(amount)

    fun findPreviousForLesson(lessonId: Int, amount: Int): List<OccurrenceEntity> = occurrenceRepository.findPreviousForLesson(lessonId, amount)

    fun getNextOccurrence(lessonId: Int): Transaction<OccurrenceEntity> {
        log.debug("Getting the next occurrence of lesson $lessonId")
        val lesson = lessonService.findLesson(lessonId).copy() // See the comment above the class

        val indexTransaction = IndexQueue(lesson).obtain().next()
        val getIndex = { indexTransaction.data }

        val dateTransaction = dateQueueService.obtain(lesson, getIndex).next()
        val userTransaction = userQueueService.obtain(lesson, getIndex).next()

        val occurrence = OccurrenceEntity(
            0,
            lessonId,
            getIndex(),
            Date.valueOf(dateTransaction.data.first.toLocalDate()),
            Time.valueOf(dateTransaction.data.first.toLocalTime()),
            userTransaction.data.first,
        )
        log.debug("Determined next occurrence of lesson with id $lessonId: ${occurrence.json()}")


        return Transaction(occurrence) {
            dateTransaction.commit()
            userTransaction.commit()
            indexTransaction.commit()

            lessonService.saveLesson(lesson)
            occurrenceRepository.save(occurrence)
            log.debug("A new occurrence of lesson ${it.lessonId} has been committed: ${it.json()}")
        }


    }

    private final tailrec fun commitPast(lessonId: Int) {
        log.debug("Committing past occurrences for lesson with id: $lessonId")
        val occurrenceTransaction = getNextOccurrence(lessonId)
        val lesson = lessonService.findLesson(lessonId).copy() // See a comment above the class

        if(!LocalDateTime.of(occurrenceTransaction.data.date.toLocalDate(), lesson.time.toLocalTime()).isBefore(LocalDateTime.now())) return
        log.debug("Committing a new occurrence: ${occurrenceTransaction.data}")

        occurrenceTransaction.commit()
        commitPast(lessonId)
    }


    fun peekOccurrences(lessonId: Int): Iterator<OccurrenceEntity> {
        commitPast(lessonId)
        log.debug("Constructing peek occurrence iterator")
        val lesson = lessonService.findLesson(lessonId).copy() // See a comment above the class

        val indexQueue = IndexQueue(lesson).peek()
        val getIndex = { indexQueue.next().data }

        return PeekOccurrenceTransactionIterator(
            dateQueueService.peek(lesson, getIndex),
            userQueueService.peek(lesson,getIndex),
            indexQueue,
            lessonId
        ).asSequence()
            .map {
                log.debug("Next occurrence is being peeked. Committing.")
                it.commit()
                it.data
            }
            .iterator()
    }

    fun updateOccurrences() = lessonService.findAllLessons().forEach { commitPast(it.id) }

    fun doesUserOccur(lessonId: Int, index: Int, userId: Int): Boolean {
        val lesson = lessonService.findLesson(lessonId).copy() // // See a comment above the class

        val indexQueue = IndexQueue(lesson).peek()
        var currentIndexTransaction = indexQueue.next()

        if(index < currentIndexTransaction.data) return false

        val userQueue = userQueueService.peek(lesson) { currentIndexTransaction.data }

        while(currentIndexTransaction.data < index) {
            currentIndexTransaction.commit()
            currentIndexTransaction = indexQueue.next()
            userQueue.next().commit()
        }

        val result =  userQueue.next().data.first == userId
        log.debug("Checked if user with id $userId occurs on the index $index of lesson with id $lessonId - $result")
        return result
    }

    private fun peekNextDates(lessonId: Int): Iterator<DateTransaction> {
        val lesson = lessonService.findLesson(lessonId).copy() // See a comment above the class
        val indexQueue = IndexQueue(lesson).peek()
        val dateQueue: Iterator<DateTransaction> = dateQueueService.peek(lesson) { indexQueue.next().data }

        return PeekDateTransactionIterator(
            indexQueue,
            dateQueue,
        )
    }

    fun createIndexToDateMapper(): IndexToDateTimeMapper = CachingIndexToDateTimeMapper()



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
                Date.valueOf(dateTransaction.data.first.toLocalDate()),
                Time.valueOf(dateTransaction.data.first.toLocalTime()),
                userTransaction.data.first,
            )

            return Transaction(occurrence) {
                dateTransaction.commit()
                userTransaction.commit()
                indexTransaction.commit()
            }

        }
    }

    interface IndexToDateTimeMapper {

        fun mapDate(lessonId: Int, index: Int): LocalDateTime

    }

    private inner class CachingIndexToDateTimeMapper : IndexToDateTimeMapper {

        private val cache: HashMap<Int, LessonCache> = HashMap()

        override fun mapDate(lessonId: Int, index: Int): LocalDateTime {
            log.trace("Mapping lesson id $lessonId and index $index to date and time")

            val lessonCache = cache.computeIfAbsent(lessonId) {
                log.trace("Creating cache for lesson id $lessonId")
                LessonCache(
                    peekNextDates(lessonId),
                    ArrayList()
                )
            }

            while(lessonCache.dates.lastOrNull()?.second?: 0 < index) {
                log.trace("Current highest index for lessonId $lessonId is ${ lessonCache.dates.lastOrNull()?.second?: 0 }, required" +
                        "index is $index, fetching more dates.")
                val transaction = lessonCache.dateIterator.next()
                log.trace("Computed next date: ${transaction.data.first}")
                transaction.commit()
                lessonCache.dates.add(transaction.data)
            }


            val mapped =  lessonCache.dates.getOrNull(index-lessonCache.dates.first().second)?.first
                ?: occurrenceRepository.findPrevious(lessonId, index).run { LocalDateTime.of(date.toLocalDate(), time.toLocalTime()) }

            log.debug("Mapped lesson id $lessonId and index $index to $mapped")
            return mapped
        }

        private inner class LessonCache(
            val dateIterator: Iterator<DateTransaction>,
            val dates: MutableList<Pair<LocalDateTime, Int>>,
        )
    }
}