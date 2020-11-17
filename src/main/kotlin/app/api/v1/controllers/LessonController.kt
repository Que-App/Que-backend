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

    @GetMapping("/api/v1/lesson/{lessonId}")
    fun findLessonById(@PathVariable("lessonId") lessonId: Int): LessonPojo =
        lessonService.findLesson(lessonId).mapToPojo()

    @GetMapping("/api/v1/subject/{subjectId}/lesson")
    fun findLessonsForSubject(@PathVariable("subjectId") id: Int) = subjectService.findSubject(id).lessonEntities.mapToLessonPojos()

    @GetMapping("/api/v1/subject/{subjectId}/lesson/{lessonId}")
    fun findLessonFromSubject(@PathVariable("subjectId") subjectId: Int, @PathVariable("lessonId") lessonId: Int)
            = subjectService.findSubject(subjectId).lessonEntities.mapToLessonPojos()
        .find { it.id == lessonId } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found")

    @GetMapping("/api/v1/lesson/{lessonId}/user")
    fun getUsersFromLesson(@PathVariable("subjectId") subjectId: Int, @PathVariable("lessonId") lessonId: Int)
            = lessonService.findLesson(subjectId).users

    @PostMapping("/api/v1/subject/{subjectId}/lesson")
    fun saveLessonToSubject(@PathVariable("subjectId") id: Int, @RequestBody lessonEntity: LessonEntity)
            = subjectService.findSubject(id).apply {
        lessonEntities.add(lessonEntity)
        subjectService.saveSubject(this)
    }
        .ok()

    @DeleteMapping("/api/v1/subject/{subjectId}/lesson/{lessonId}")
    fun deleteLessonFromSubject(@PathVariable("subjectId") subjectId: Int, @PathVariable("lessonId") lessonId: Int)
            = subjectService.findSubject(subjectId).apply {
        lessonEntities.remove(lessonEntities.first { it.id == lessonId })
        subjectService.saveSubject(this)
    }
        .ok()
}