package app.api.v1.controllers

import app.api.v1.pojos.SubjectPojo
import app.api.v1.pojos.mapping.mapToPojo
import app.api.v1.pojos.mapping.mapToSubjectPojos
import app.services.SubjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class SubjectController {

    @Autowired
    private lateinit var subjectService: SubjectService

    @GetMapping("/api/v1/subjects")
    fun findSubjects(): List<SubjectPojo> = subjectService.findAllSubjects().mapToSubjectPojos()

    @GetMapping("/api/v1/subjects/{subjectId}")
    fun findSubjectById(@PathVariable("subjectId") id: Int): SubjectPojo =
        subjectService.findSubject(id).mapToPojo()
}