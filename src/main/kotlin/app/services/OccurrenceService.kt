package app.services

import app.data.entities.OccurrenceEntity
import app.data.repositories.OccurrenceRepository
import engine.core.Lesson
import engine.util.OccurrenceTransaction
import engine.util.Transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
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

        val occurrence: OccurrenceTransaction =  OccurrenceTransaction(lessonId, dateToLesson.second.entity.lessonindex, dateToLesson.first, user)
            .onCommit { saveOccurrence(it) } as OccurrenceTransaction

        changeService.apply(occurrence)

        return if (!occurrence.aborted) occurrence else getOccurrence(lessonId)
    }

    final tailrec fun peekOccurrence(lessonId: Int, amount: Int,
                                             acc: LinkedList<OccurrenceEntity> = LinkedList(),
                                             users: Iterator<Transaction<Int>> = queueService.peekQueueIterator(lessonId),
                                             dates: Iterator<Pair<Transaction<Date>, Lesson>> = lessonService.peekNextDates(lessonId))
            : List<OccurrenceEntity> {

        val user: Transaction<Int> = users.next()
        val dateToLesson: Pair<Transaction<Date>, Lesson> = dates.next()

        val occurrence =  OccurrenceTransaction(
            dateToLesson.second.entity.id,
            dateToLesson.second.entity.lessonindex,
            dateToLesson.first,
            user,
        )

        changeService.mockApply(occurrence)
        occurrence.commit()

        if(!occurrence.aborted) acc.add(occurrence.data)

        return if (acc.size == amount) acc
        else peekOccurrence(lessonId, amount, acc, users, dates)

    }

}