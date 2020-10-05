package app.services

import app.data.entities.OccurrenceEntity
import app.data.entities.changes.BaseChangeEntity
import app.data.entities.changes.CancelDateChangeEntity
import app.data.entities.changes.DateChangeEntity
import app.data.repositories.changes.CancelDateChangeRepository
import app.data.repositories.changes.DateChangeRepository
import engine.util.OccurrenceTransaction
import engine.filter.Filter
import engine.filter.chain.OccurrenceTransactionFilterChain
import engine.filter.exceptions.UnrecognizedChangeTypeException
import engine.filter.manager.OccurrenceTransactionFilterManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChangeService {

    @Autowired
    lateinit var dateChangeRepository: DateChangeRepository

    @Autowired
    lateinit var cancelDateChangeRepository: CancelDateChangeRepository

    fun getDateChanges(lessonid: UUID) = dateChangeRepository
        .findChangesForLesson(lessonid.toString())
        .toList()

    fun getCancelDateChanges(lessonid: UUID) = cancelDateChangeRepository
        .findChangesForLesson(lessonid.toString())
        .toList()

    fun createFilterManager(lessonid: UUID): OccurrenceTransactionFilterManager =
        OccurrenceTransactionFilterManager(listOf(
            OccurrenceTransactionFilterChain(getDateChanges(lessonid).map { compile(it) }),
            OccurrenceTransactionFilterChain(getCancelDateChanges(lessonid).map { compile(it) })
            ))

    fun createMockupFilterManager(lessonid: UUID): OccurrenceTransactionFilterManager =
        OccurrenceTransactionFilterManager(listOf(
            OccurrenceTransactionFilterChain(getDateChanges(lessonid).map { mockCompile(it) }),
            OccurrenceTransactionFilterChain(getCancelDateChanges(lessonid).map { mockCompile(it) })
        ))


    private fun onChangeApplied(changeEntity: BaseChangeEntity): Unit = when (changeEntity) {
        is DateChangeEntity -> dateChangeRepository.delete(changeEntity)
        is CancelDateChangeEntity -> cancelDateChangeRepository.delete(changeEntity)
        else -> throw UnrecognizedChangeTypeException("Unrecognized change type: ${changeEntity::class}")
    }

    private fun <T : BaseChangeEntity> mockCompile(change: T): Filter<OccurrenceTransaction> = when(change) {
        is DateChangeEntity -> Filter { t: OccurrenceTransaction ->
            if(t.data.date == change.date && t.data.lessonid == change.lessonid) {
                t.data.userid = change.user
            }
        }

        is CancelDateChangeEntity -> Filter { t: OccurrenceTransaction ->
            if(t.data.lessonid == change.lessonid && t.data.date == change.date) {
                t.dateTransaction.commit()
                t.abort()
            }
        }
        else -> throw UnrecognizedChangeTypeException("Unrecognized change type: ${change::class.qualifiedName}")
    }

    private fun <T : BaseChangeEntity> compile(change: T): Filter<OccurrenceTransaction> {
            val mockFun = mockCompile(change)
        return Filter { mockFun.filter(it); it.onCommit { onChangeApplied(change) } }
    }

    fun apply(transaction: OccurrenceTransaction): OccurrenceTransaction {
        createFilterManager(transaction.data.lessonid).filter(transaction)
        return transaction
    }

    fun mockApply(transaction: OccurrenceTransaction): OccurrenceTransaction {
        createMockupFilterManager(transaction.data.lessonid).filter(transaction)
        return transaction
    }
}


