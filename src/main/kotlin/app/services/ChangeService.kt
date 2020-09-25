package app.services

import app.data.entities.OccurrenceEntity
import app.data.entities.changes.BaseChangeEntity
import app.data.entities.changes.CancelDateChangeEntity
import app.data.entities.changes.DateChangeEntity
import app.data.repositories.changes.CancelDateChangeRepository
import app.data.repositories.changes.DateChangeRepository
import engine.util.OccurrenceTransaction
import engine.filter.Filter
import engine.filter.chain.TransactionFilterChain
import engine.filter.exceptions.UnrecognizedChangeTypeException
import engine.util.Transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChangeService {

    @Autowired
    lateinit var dateChangeRepository: DateChangeRepository

    @Autowired
    lateinit var cancelDateChangeRepository: CancelDateChangeRepository

    private fun getChangesForLesson(lessonid: UUID): List<BaseChangeEntity> = LinkedList<BaseChangeEntity>().apply {
        addAll(dateChangeRepository.findAll().filter { it.lessonid == lessonid })
        addAll(cancelDateChangeRepository.getChangesForLesson(lessonid.toString()))
    }


    private fun onChangeApplied(changeEntity: BaseChangeEntity): Unit = when (changeEntity) {
        is DateChangeEntity -> dateChangeRepository.delete(changeEntity)
        is CancelDateChangeEntity -> cancelDateChangeRepository.delete(changeEntity)
        else -> throw UnrecognizedChangeTypeException("Unrecognized change type: ${changeEntity::class}")
    }

    private fun <T : BaseChangeEntity> mockCompile(change: T): (Transaction<OccurrenceEntity>) -> Unit = when(change) {
        is DateChangeEntity -> { t: Transaction<OccurrenceEntity> ->
            if(t.data.date == change.date && t.data.lessonid == change.lessonid) {
                t.data.userid = change.user
            }
        }

        is CancelDateChangeEntity -> { t: Transaction<OccurrenceEntity> ->
            if(t.data.lessonid == change.lessonid && t.data.date == change.date) {
                (t as OccurrenceTransaction).dateTransaction.commit()
                t.abort()
            }
        }
        else -> throw UnrecognizedChangeTypeException("Unrecognized change type: ${change::class.qualifiedName}")
    }

    private fun <T : BaseChangeEntity> compile(change: T): (Transaction<OccurrenceEntity>) -> Unit {
            val mockFun = mockCompile(change)
        return { mockFun.invoke(it); it.onCommit { onChangeApplied(change) } }
    }

    fun apply(transaction: OccurrenceTransaction): OccurrenceTransaction {
        TransactionFilterChain(
            getChangesForLesson(transaction.data.lessonid)
                .map { c -> Filter<Transaction<OccurrenceEntity>> {
                    compile(c).invoke(it) }
                }
        ).filter(transaction)

        return transaction
    }

    fun mockApply(transaction: OccurrenceTransaction): OccurrenceTransaction {
        TransactionFilterChain(
            getChangesForLesson(transaction.data.lessonid)
                .map { c -> Filter<Transaction<OccurrenceEntity>> {
                    mockCompile(c).invoke(it) }
                }
        ).filter(transaction)

        return transaction
    }

}