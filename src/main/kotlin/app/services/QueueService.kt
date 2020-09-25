package app.services

import app.data.entities.QueueEntity
import app.data.repositories.QueueRepository
import engine.core.Queue
import engine.util.Transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class QueueService {

    @Autowired
    lateinit var queueRepository: QueueRepository

    fun saveQueue(queueEntity: QueueEntity) = queueRepository.save(queueEntity)

    fun peekQueue(lessonId: UUID, amount: Int): List<Transaction<UUID>> = queueRepository.findById(lessonId)
        .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found.") }
        .run { Queue(this) }
        .peek(amount)
        .map { Transaction(it) {} }

    fun getFromQueue(lessonId: UUID): Transaction<UUID> =
        queueRepository.findById(lessonId)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found") }
            .run { Queue(this) }
            .run { get().onCommit { queueRepository.save( getEntity() ) } }


}