package app.api.v1.controllers

import app.api.v1.pojos.LessonPojo
import app.api.v1.pojos.mapToLessonPojos
import app.api.v1.pojos.mapToPojo
import app.data.entities.LessonEntity
import app.services.LessonService
import app.services.SubjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import util.ok

@CrossOrigin
@RestController
class LessonController {

    @Autowired
    private lateinit var subjectService: SubjectService

    @Autowired
    private lateinit var lessonService: LessonService

    @GetMapping("/api/v1/lesson/{lessonid}")
    fun findLessonById(@PathVariable("lessonid") lessonid: Int): LessonPojo =
        lessonService.findLesson(lessonid).mapToPojo()

    @GetMapping("/api/v1/subject/{subjectid}/lesson")
    fun findLessonsForSubject(@PathVariable("subjectid") id: Int) = subjectService.findSubject(id).lessonEntities.mapToLessonPojos()

    @GetMapping("/api/v1/subject/{subjectid}/lesson/{lessonid}")
    fun findLessonFromSubject(@PathVariable("subjectid") subjectid: Int, @PathVariable("lessonid") lessonid: Int)
            = subjectService.findSubject(subjectid).lessonEntities.mapToLessonPojos()
        .find { it.id == lessonid } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found")

    @GetMapping("/api/v1/lesson/{lessonid}/user")
    fun getUsersFromLesson(@PathVariable("subjectid") subjectid: Int, @PathVariable("lessonid") lessonid: Int)
            = lessonService.findLesson(subjectid).users

    @PostMapping("/api/v1/subject/{subjectid}/lesson")
    fun saveLessonToSubject(@PathVariable("subjectid") id: Int, @RequestBody lessonEntity: LessonEntity)
            = subjectService.findSubject(id).apply {
        lessonEntities.add(lessonEntity)
        subjectService.saveSubject(this)
    }
        .ok()

    @DeleteMapping("/api/v1/subject/{subjectid}/lesson/{lessonid}")
    fun deleteLessonFromSubject(@PathVariable("subjectid") subjectid: Int, @PathVariable("lessonid") lessonid: Int)
            = subjectService.findSubject(subjectid).apply {
        lessonEntities.remove(lessonEntities.first { it.id == lessonid })
        subjectService.saveSubject(this)
    }
        .ok()
}