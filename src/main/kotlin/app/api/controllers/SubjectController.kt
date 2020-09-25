package app.api.controllers

import app.api.pojos.SubjectPojo
import app.api.pojos.mapToSubjectPojos
import app.data.entities.SubjectEntity
import app.services.SubjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

@CrossOrigin
@RestController
class SubjectController {

    @Autowired
    lateinit var subjectService: SubjectService

    @RequestMapping("/subject", method = [RequestMethod.GET])
    fun findSubjects(): List<SubjectPojo> = subjectService.findAllSubjects().mapToSubjectPojos()

    @RequestMapping("/subject", method = [RequestMethod.POST])
    fun saveSubject(@RequestBody subjectPojo: SubjectPojo) =
        subjectService.saveSubject(
            SubjectEntity(UUID.randomUUID(), subjectPojo.name, subjectPojo.teacher, LinkedList())
        )

    @RequestMapping("/subject/{subjectid}", method = [RequestMethod.DELETE])
    fun deleteSubject(@PathVariable("subjectid") id: UUID) = subjectService.deleteSubject(id)
}



