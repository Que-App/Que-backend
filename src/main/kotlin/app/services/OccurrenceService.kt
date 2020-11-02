package app.services

import app.data.entities.OccurrenceEntity
import app.data.repositories.OccurrenceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OccurrenceService {

    @Autowired
    private lateinit var occurrenceRepository: OccurrenceRepository

    fun saveOccurrence(occurrence: OccurrenceEntity) =
        occurrenceRepository.save(occurrence)

    fun findAllOccurrences() =
        occurrenceRepository.findAll()

    fun findPrevious(lessonId: Int, amount: Int): List<OccurrenceEntity> =
        occurrenceRepository.findPrevious(lessonId, amount)

}