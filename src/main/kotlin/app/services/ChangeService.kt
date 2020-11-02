package app.services

import app.data.entities.changes.BaseChangeEntity
import app.data.entities.changes.CancelDateChangeEntity
import app.data.entities.changes.DateChangeEntity
import app.data.repositories.changes.CancelDateChangeRepository
import app.data.repositories.changes.DateChangeRepository
import engine.filter.Filter
import engine.filter.chain.OccurrenceTransactionFilterChain
import engine.filter.exceptions.UnrecognizedChangeTypeException
import engine.filter.manager.OccurrenceTransactionFilterManager
import engine.util.OccurrenceTransaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChangeService {

    @Autowired
    private lateinit var dateChangeRepository: DateChangeRepository

    @Autowired
    private lateinit var cancelDateChangeRepository: CancelDateChangeRepository

    fun getDateChanges(lessonid: Int) = dateChangeRepository
        .findChangesForLesson(lessonid.toString())
        .toList()

    fun getCancelDateChanges(lessonid: Int) = cancelDateChangeRepository
        .findChangesForLesson(lessonid.toString())
        .toList()

    fun createFilterManager(lessonid: Int): OccurrenceTransactionFilterManager =
        OccurrenceTransactionFilterManager(listOf(
            OccurrenceTransactionFilterChain(getDateChanges(lessonid).map { compile(it) }),
            OccurrenceTransactionFilterChain(getCancelDateChanges(lessonid).map { compile(it) })
            ))

    fun createMockupFilterManager(lessonid: Int): OccurrenceTransactionFilterManager =
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
                t.data.userid = change.userid
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
        createFilterManager(transaction.data.lessonid?: throw NullPointerException("Attempted to apply changes to occurrence which lesson was removed."))
            .filter(transaction)
        return transaction
    }

    fun mockApply(transaction: OccurrenceTransaction): OccurrenceTransaction {
        createMockupFilterManager(transaction.data.lessonid?: throw NullPointerException("Attempted to apply changes to occurrence which lesson was removed."))
            .filter(transaction)
        return transaction
    }
}


