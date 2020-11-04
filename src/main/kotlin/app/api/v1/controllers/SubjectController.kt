package app.api.v1.controllers

import app.api.v1.pojos.SubjectPojo
import app.api.v1.pojos.mapToPojo
import app.api.v1.pojos.mapToSubjectPojos
import app.data.entities.SubjectEntity
import app.services.SubjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import util.ok
import java.util.*

@CrossOrigin
@RestController
class SubjectController {

    @Autowired
    private lateinit var subjectService: SubjectService

    @RequestMapping("/api/v1/subject", method = [RequestMethod.GET])
    fun findSubjects(): List<SubjectPojo> = subjectService.findAllSubjects().mapToSubjectPojos()

    @RequestMapping("/api/v1/subject/{subjectid}")
    fun findSubjectById(@PathVariable("subjectid") id: Int): SubjectPojo =
        subjectService.findSubject(id).mapToPojo()

    @RequestMapping("/api/v1/subject", method = [RequestMethod.POST])
    fun saveSubject(@RequestBody subjectPojo: SubjectPojo) =
        subjectService.saveSubject(
            SubjectEntity(0, subjectPojo.name, subjectPojo.teacher, LinkedList())
        )
            .ok()

    @RequestMapping("/api/v1/subject/{subjectid}", method = [RequestMethod.DELETE])
    fun deleteSubject(@PathVariable("subjectid") id: Int) = subjectService.deleteSubject(id)
        .ok()
}



