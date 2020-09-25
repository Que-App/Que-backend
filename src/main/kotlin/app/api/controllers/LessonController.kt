package app.api.controllers

import app.data.entities.LessonEntity
import app.services.LessonService
import app.services.SubjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@CrossOrigin
@RestController
class LessonController {

    @Autowired
    lateinit var subjectService: SubjectService

    @Autowired
    lateinit var lessonService: LessonService

    @RequestMapping("/subject/{subjectid}/lesson", method = [RequestMethod.GET])
    fun findLessonsForSubject(@PathVariable("subjectid") id: UUID) = subjectService.findSubject(id).lessonEntities

    @RequestMapping("/subject/{subjectid}/lesson/{lessonid}", method = [RequestMethod.GET])
    fun findLessonFromSubject(@PathVariable("subjectid") subjectid: UUID, @PathVariable("lessonid") lessonid: UUID)
            = subjectService.findSubject(subjectid).lessonEntities
        .find { it.id == lessonid } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found")

    @RequestMapping("/lesson/{lessonid}/user", method = [RequestMethod.GET])
    fun getUsersFromLesson(@PathVariable("subjectid") subjectid: UUID, @PathVariable("lessonid") lessonid: UUID)
            = lessonService.findLesson(subjectid).queue.users

    @RequestMapping("/subject/{subjectid}/lesson", method = [RequestMethod.POST])
    fun saveLessonToSubject(@PathVariable("subjectid") id: UUID, @RequestBody lessonEntity: LessonEntity)
            = subjectService.findSubject(id).apply {
        lessonEntities.add(lessonEntity)
        subjectService.saveSubject(this)
    }

    @RequestMapping("/subject/{subjectid}/lesson/{lessonid}", method = [RequestMethod.DELETE])
    fun deleteLessonFromSubject(@PathVariable("subjectid") subjectid: UUID, @PathVariable("lessonid") lessonid: UUID)
            = subjectService.findSubject(subjectid).apply {
        lessonEntities.remove(lessonEntities.first { it.id == lessonid })
        subjectService.saveSubject(this)
    }
}