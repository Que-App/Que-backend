package app.api.v1.controllers

import app.api.v1.pojos.LessonPojo
import app.api.v1.pojos.mapToLessonPojos
import app.api.v1.pojos.mapToPojo
import app.data.entities.LessonEntity
import app.services.LessonService
import app.services.SubjectService
import lib.returnUnit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@CrossOrigin
@RestController
class LessonController {

    @Autowired
    private lateinit var subjectService: SubjectService

    @Autowired
    private lateinit var lessonService: LessonService

    @RequestMapping("/api/v1/lesson/{lessonid}")
    fun findLessonById(@PathVariable("lessonid") lessonid: Int): LessonPojo =
        lessonService.findLesson(lessonid).mapToPojo()

    @RequestMapping("/api/v1/subject/{subjectid}/lesson", method = [RequestMethod.GET])
    fun findLessonsForSubject(@PathVariable("subjectid") id: Int) = subjectService.findSubject(id).lessonEntities.mapToLessonPojos()

    @RequestMapping("/api/v1/subject/{subjectid}/lesson/{lessonid}", method = [RequestMethod.GET])
    fun findLessonFromSubject(@PathVariable("subjectid") subjectid: Int, @PathVariable("lessonid") lessonid: Int)
            = subjectService.findSubject(subjectid).lessonEntities.mapToLessonPojos()
        .find { it.id == lessonid } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found")

    @RequestMapping("/api/v1/lesson/{lessonid}/user", method = [RequestMethod.GET])
    fun getUsersFromLesson(@PathVariable("subjectid") subjectid: Int, @PathVariable("lessonid") lessonid: Int)
            = lessonService.findLesson(subjectid).users

    @RequestMapping("/api/v1/subject/{subjectid}/lesson", method = [RequestMethod.POST])
    fun saveLessonToSubject(@PathVariable("subjectid") id: Int, @RequestBody lessonEntity: LessonEntity)
            = subjectService.findSubject(id).apply {
        lessonEntities.add(lessonEntity)
        subjectService.saveSubject(this)
    }
        .returnUnit()

    @RequestMapping("/api/v1/subject/{subjectid}/lesson/{lessonid}", method = [RequestMethod.DELETE])
    fun deleteLessonFromSubject(@PathVariable("subjectid") subjectid: Int, @PathVariable("lessonid") lessonid: Int)
            = subjectService.findSubject(subjectid).apply {
        lessonEntities.remove(lessonEntities.first { it.id == lessonid })
        subjectService.saveSubject(this)
    }
        .returnUnit()
}