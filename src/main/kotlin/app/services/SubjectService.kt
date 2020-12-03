package app.services

import app.data.entities.SubjectEntity
import app.data.repositories.SubjectRepository
import app.services.exceptions.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SubjectService {

    @Autowired
    private lateinit var subjectRepository: SubjectRepository

    fun findAllSubjects(): List<SubjectEntity> = subjectRepository.findAll().toList()

    fun findSubject(id: Int): SubjectEntity = subjectRepository
        .findById(id)
        .orElseThrow {
        throw EntityNotFoundException("subject")
    }

    fun saveSubject(subjectEntity: SubjectEntity): SubjectEntity = subjectRepository.save(subjectEntity)

    fun deleteSubject(subjectEntity: SubjectEntity) = subjectRepository.delete(subjectEntity)

    fun deleteSubject(id: Int) = subjectRepository.deleteById(id)

}