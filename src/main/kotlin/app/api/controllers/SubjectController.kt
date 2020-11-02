package app.api.controllers

import app.api.pojos.SubjectPojo
import app.api.pojos.mapToPojo
import app.api.pojos.mapToSubjectPojos
import app.data.entities.SubjectEntity
import app.services.SubjectService
import lib.returnUnit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

@CrossOrigin
@RestController
class SubjectController {

    @Autowired
    private lateinit var subjectService: SubjectService

    @RequestMapping("/subject", method = [RequestMethod.GET])
    fun findSubjects(): List<SubjectPojo> = subjectService.findAllSubjects().mapToSubjectPojos()

    @RequestMapping("/subject/{subjectid}")
    fun findSubjectById(@PathVariable("subjectid") id: Int): SubjectPojo =
        subjectService.findSubject(id).mapToPojo()

    @RequestMapping("/subject", method = [RequestMethod.POST])
    fun saveSubject(@RequestBody subjectPojo: SubjectPojo) =
        subjectService.saveSubject(
            SubjectEntity(0, subjectPojo.name, subjectPojo.teacher, LinkedList())
        )
            .returnUnit()

    @RequestMapping("/subject/{subjectid}", method = [RequestMethod.DELETE])
    fun deleteSubject(@PathVariable("subjectid") id: Int) = subjectService.deleteSubject(id)
        .returnUnit()
}



