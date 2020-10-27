package app.api.controllers

import app.api.pojos.OccurrencePojo
import app.data.entities.OccurrenceEntity
import app.services.*
import engine.core.Lesson
import engine.util.OccurrenceTransaction
import engine.util.Transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.sql.Date
import java.time.LocalDate
import java.util.*

@CrossOrigin
@RestController
class QueueController {

    @Autowired
    lateinit var lessonService: LessonService

    @Autowired
    lateinit var queueService: QueueService

    @Autowired
    lateinit var changeService: ChangeService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var occurrenceService: OccurrenceService



    //Queue
    @RequestMapping("/queue/{lessonid}/{amount}", method = [RequestMethod.GET])
    fun getQueue(@PathVariable lessonid: Int, @PathVariable("amount") amount: Int): List<OccurrencePojo> {
        commitPast(lessonid)

        return peekOccurrence(lessonid, amount).map {
            val userPojo = userService.findUser(it.userid!!)
            OccurrencePojo(it.lessonid, it.userid, it.lessonindex, it.date, userPojo.username)
        }
    }

    private final tailrec fun commitPast(lessonid: Int) {
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
            .onCommit { occurrenceService.saveOccurrence(it) } as OccurrenceTransaction

        changeService.apply(occurrence)

        return if (!occurrence.aborted) occurrence else getOccurrence(lessonId)
    }

    private final tailrec fun peekOccurrence(lessonId: Int, amount: Int,
                                             acc: LinkedList<OccurrenceEntity> = LinkedList(),
                                             users: Iterator<Transaction<Int>> = queueService.peekQueueIterator(lessonId),
                                             dates: Iterator<Pair<Transaction<Date>, Lesson>> = lessonService.peekNextDates(lessonId))
            : List<OccurrenceEntity> {

        val user: Transaction<Int> = users.next()
        val dateToLesson: Pair<Transaction<Date>, Lesson> = dates.next()

        val occurrence: OccurrenceTransaction =  OccurrenceTransaction(
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
