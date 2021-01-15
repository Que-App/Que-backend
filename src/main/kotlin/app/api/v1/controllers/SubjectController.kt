package app.api.v1.controllers

import app.api.v1.pojos.SubjectPojo
import app.api.v1.pojos.mapping.mapToPojo
import app.api.v1.pojos.mapping.mapToSubjectPojos
import app.data.entities.SubjectEntity
import app.services.SubjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import util.ok
import java.util.*
import javax.validation.Valid

@CrossOrigin
@RestController
class SubjectController {

    @Autowired
    private lateinit var subjectService: SubjectService

    @GetMapping("/api/v1/subjects")
    fun findSubjects(): List<SubjectPojo> = subjectService.findAllSubjects().mapToSubjectPojos()

    @GetMapping("/api/v1/subjects/{subjectId}")
    fun findSubjectById(@PathVariable("subjectId") id: Int): SubjectPojo =
        subjectService.findSubject(id).mapToPojo()

    @PostMapping("/api/v1/subjects")
    fun saveSubject(@Valid @RequestBody subjectPojo: SubjectPojo) =
        subjectService.saveSubject(
            SubjectEntity(0, subjectPojo.name!!, subjectPojo.teacher!!, LinkedList())
        )
            .ok()

    @DeleteMapping("/api/v1/subjects/{subjectId}")
    fun deleteSubject(@PathVariable("subjectId") id: Int) = subjectService.deleteSubject(id)
        .ok()
}