package app.services

import app.data.entities.LessonEntity
import app.data.entities.changes.UserChangeEntity
import app.data.repositories.changes.UserChangeRepository
import engine.core.UserQueue
import engine.util.Transaction
import engine.util.UserTransaction
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import util.json

@Service
class UserQueueService {

    companion object {
        private val log: Logger = LogManager.getLogger()
    }

    @Autowired
    private lateinit var changeRepository: UserChangeRepository

    fun saveUserChange(change: UserChangeEntity) = changeRepository.save(change)


    private fun compileUserChange(
        change: UserChangeEntity,
        dispose: (UserChangeEntity) -> Unit,
    ): (Iterator<UserTransaction>) -> Iterator<UserTransaction> = { source ->
        source
            .asSequence()
            .map {
                log.debug("Change ${change.json()} is executing")
                if(it.data.second == change.lessonIndex) {
                    log.debug("Change ${change.json()} is being applied")
                    it.commit()
                    Transaction(change.userId to it.data.second) { dispose(change) }
                } else it
            }
            .iterator()

    }

    private fun applyChanges(
        source: Iterator<UserTransaction>,
        changes: Collection<(Iterator<UserTransaction>) -> Iterator<UserTransaction>>,
    ): Iterator<UserTransaction> =
        changes
            .reduceOrNull { acc, change -> { change(acc(it)) } }
            ?.invoke(source)?: source


    fun obtain(lesson: LessonEntity, getIndex: () -> Int): Iterator<UserTransaction> {
        val changes = changeRepository.findMostRecentForIndexes(lesson.id).map { change ->
            compileUserChange(change) { changeRepository.delete(it); log.debug("Change ${ it.json() } disposed.") }
        }

        val queue = UserQueue(lesson, getIndex).obtain()

        return applyChanges(queue, changes)

    }

    fun peek(lesson: LessonEntity, getIndex: () -> Int): Iterator<UserTransaction> {
        val changes = changeRepository.findMostRecentForIndexes(lesson.id).map { change ->
            compileUserChange(change) { log.debug("Change ${ it.json() } is applied but in peek mode - not disposing") }
        }

        val queue = UserQueue(lesson, getIndex).peek()

        return applyChanges(queue, changes)

    }


}