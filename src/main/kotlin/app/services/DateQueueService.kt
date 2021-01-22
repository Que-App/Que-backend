package app.services

import app.data.entities.LessonEntity
import app.data.entities.changes.CancelDateChangeEntity
import app.data.entities.changes.DateChangeEntity
import app.data.repositories.changes.CancelDateChangeRepository
import app.data.repositories.changes.DateChangeRepository
import engine.core.DateQueue
import engine.util.DateTransaction
import engine.util.Transaction
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import util.localDateTime


@Service
class DateQueueService {

    companion object {
        private val log: Logger = LogManager.getLogger()
    }

    @Autowired
    private lateinit var cancelDateChangeRepository: CancelDateChangeRepository

    @Autowired
    private lateinit var dateChangeRepository: DateChangeRepository

    private fun compileCancelDateChange(
        change: CancelDateChangeEntity,
        dispose: (CancelDateChangeEntity) -> Unit,
    ): (Iterator<DateTransaction>) -> Iterator<DateTransaction> = { source ->
        source
            .asSequence()
            .map {
                if(it.data.first.toLocalDate() == change.date.toLocalDate() && it.data.first.toLocalTime() == change.time.toLocalTime()) {
                    it.commit()
                    source.next().onCommit { dispose(change) }
                } else it
            }
            .iterator()
    }

    private fun compileDateChange(
        change: DateChangeEntity,
        dispose: (DateChangeEntity) -> Unit,
    ): (Iterator<DateTransaction>) -> Iterator<DateTransaction> = { source ->
        source
            .asSequence()
            .map {
                if(it.data.second == change.lessonIndex) {
                    it.abort()
                    Transaction(localDateTime(change.date, change.time) to it.data.second) { dispose(change) }
                }
                else it
            }
            .iterator()
    }

    private fun applyChanges(
        source: Iterator<DateTransaction>,
        date: Collection<(Iterator<DateTransaction>) -> Iterator<DateTransaction>>,
        cancel: Collection<(Iterator<DateTransaction>) -> Iterator<DateTransaction>>,
    ): Iterator<DateTransaction> {
        val appliedDate =
            date.reduceOrNull { acc, change -> { change(acc(it)) } }?.invoke(source)

        return cancel.reduceOrNull { acc, change -> { change(acc(it)) } }?.invoke(appliedDate?: source)?: appliedDate ?:source
    }


    // TODO: obtain and peek are very similar, should be able to do it without as much code duplication
    fun obtain(lesson: LessonEntity, getIndex: () -> Int): Iterator<DateTransaction> {
        val dateChanges = dateChangeRepository.findMostRecentForIndexes(lesson.id).map { change ->
            compileDateChange(change) { dateChangeRepository.delete(it) }
        }

        val cancelChanges = cancelDateChangeRepository.findMostRecentForIndexes(lesson.id).map { change ->
            compileCancelDateChange(change) { cancelDateChangeRepository.delete(it) }
        }

        val queue = DateQueue(lesson, getIndex).obtain()

        return applyChanges(queue, dateChanges, cancelChanges)
    }

    fun peek(lesson: LessonEntity, getIndex: () -> Int): Iterator<DateTransaction> {
        val dateChanges = dateChangeRepository.findMostRecentForIndexes(lesson.id).map { change ->
            compileDateChange(change) {}
        }

        val cancelChanges = cancelDateChangeRepository.findMostRecentForIndexes(lesson.id).map { change ->
            compileCancelDateChange(change) {}
        }

        val queue = DateQueue(lesson, getIndex).peek()

        return applyChanges(queue, dateChanges, cancelChanges)
    }




}