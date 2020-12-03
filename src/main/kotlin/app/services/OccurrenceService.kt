package app.services

import app.data.entities.OccurrenceEntity
import app.data.repositories.OccurrenceRepository
import engine.core.Lesson
import engine.util.OccurrenceTransaction
import engine.util.Transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import util.repeatApply
import java.sql.Date
import java.time.LocalDate
import java.util.*

@Service
class OccurrenceService {

    @Autowired
    private lateinit var occurrenceRepository: OccurrenceRepository

    @Autowired
    private lateinit var queueService: QueueService

    @Autowired
    private lateinit var lessonService: LessonService

    @Autowired
    private lateinit var changeService: ChangeService

    fun saveOccurrence(occurrence: OccurrenceEntity) =
        occurrenceRepository.save(occurrence)

    fun findAllOccurrences() =
        occurrenceRepository.findAll()

    fun findPrevious(lessonId: Int, amount: Int): List<OccurrenceEntity> =
        occurrenceRepository.findPrevious(lessonId, amount)

    final tailrec fun commitPast(lessonid: Int) {
        val occurrence = getOccurrence(lessonid)

        var contiune = false

        occurrence.onCommit { contiune = true }
        if(occurrence.data.date.before(Date.valueOf(LocalDate.now()))) occurrence.commit()

        if(contiune) commitPast(lessonid)
    }

    private final tailrec fun getOccurrence(lessonId: Int): OccurrenceTransaction {
        val user: Transaction<Int> = queueService.getFromQueue(lessonId)

        val dateToLesson: Pair<Transaction<Date>, Lesson> = lessonService.nextDate(lessonId)

        val occurrence: OccurrenceTransaction =  OccurrenceTransaction(lessonId, dateToLesson.second.entity.lessonIndex, dateToLesson.first, user)
            .onCommit { saveOccurrence(it) } as OccurrenceTransaction

        changeService.apply(occurrence)

        return if (!occurrence.aborted) occurrence else getOccurrence(lessonId)
    }

    fun peekOccurrence(
        lessonId: Int,
        amount: Int,
        acc: LinkedList<OccurrenceEntity> = LinkedList(),
        occurrencesIterator: Iterator<OccurrenceTransaction> = occurrenceIterator(lessonId)
    ): List<OccurrenceEntity> {
        val iterator = occurrenceIterator(lessonId)
        return LinkedList<OccurrenceEntity>().repeatApply(amount) {
            add(iterator.next().data)
        }
    }
    
    fun occurrenceIterator(lessonId: Int): Iterator<OccurrenceTransaction> = PeekOccurrenceIterator(
        queueService.peekQueueIterator(lessonId),
        lessonService.peekNextDatesIterator(lessonId),
        changeService
    )

    private class PeekOccurrenceIterator(
        private val usersIterator: Iterator<Transaction<Int>>,
        private val dateIterator: Iterator<Pair<Transaction<Date>, Lesson>>,
        private val changeService: ChangeService
    ) : Iterator<OccurrenceTransaction> {

        override fun hasNext(): Boolean = true

        override fun next(): OccurrenceTransaction = getNext()

        private tailrec fun getNext(): OccurrenceTransaction {
            val user: Transaction<Int> = usersIterator.next()
            val dateToLesson: Pair<Transaction<Date>, Lesson> = dateIterator.next()

            val occurrence =  OccurrenceTransaction(
                dateToLesson.second.entity.id,
                dateToLesson.second.entity.lessonIndex,
                dateToLesson.first,
                user,
            )

            changeService.mockApply(occurrence)
            occurrence.commit()
            return if(!occurrence.aborted) occurrence else getNext()
        }
    }

    fun doesUserOccur(lessonId: Int, date: Date, user: Int): Boolean {
        val iterator = occurrenceIterator(lessonId)

        var occurrence = iterator.next()

        while (!occurrence.data.date.toLocalDate().isAfter(date.toLocalDate())) {
            if(occurrence.data.date == date)
                return occurrence.data.userId == user
            occurrence = iterator.next()
        }
        return false
    }

}