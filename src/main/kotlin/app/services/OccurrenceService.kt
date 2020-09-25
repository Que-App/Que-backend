package app.services

import app.data.entities.OccurrenceEntity
import app.data.repositories.OccurrenceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OccurrenceService {

    @Autowired
    lateinit var occurrenceRepository: OccurrenceRepository

    fun saveOccurrence(occurrence: OccurrenceEntity) =
        occurrenceRepository.save(occurrence)

    fun findAllOccurrences() =
        occurrenceRepository.findAll()

    fun findPrevious(amount: Int): List<OccurrenceEntity> =
        occurrenceRepository.findPrevious(amount)

}