package app.services

import app.data.entities.SubjectEntity
import app.data.repositories.SubjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class SubjectService {

    @Autowired
    lateinit var subjectRepository: SubjectRepository

    fun findAllSubjects(): List<SubjectEntity> = subjectRepository.findAll().toList()

    fun findSubject(id: Int): SubjectEntity = subjectRepository.findById(id).orElseThrow {
        ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found")
    }

    fun saveSubject(subjectEntity: SubjectEntity): SubjectEntity = subjectRepository.save(subjectEntity)

    fun deleteSubject(subjectEntity: SubjectEntity) = subjectRepository.delete(subjectEntity)

    fun deleteSubject(id: Int) = subjectRepository.deleteById(id)

}