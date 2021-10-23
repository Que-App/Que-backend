package app.api.v1.controllers

import app.api.v1.pojos.LessonPojo
import app.api.v1.pojos.mapping.mapToLessonPojos
import app.api.v1.pojos.mapping.mapToPojo
import app.services.LessonService
import app.services.SubjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class LessonController {

    @Autowired
    private lateinit var subjectService: SubjectService

    @Autowired
    private lateinit var lessonService: LessonService

    @GetMapping("/api/v1/lessons/{lessonId}")
    fun findLessonById(@PathVariable("lessonId") lessonId: Int): LessonPojo =
        lessonService.findLesson(lessonId).mapToPojo()

    @GetMapping("/api/v1/subjects/{subjectId}/lessons")
    fun findLessonsForSubject(@PathVariable("subjectId") id: Int) =
        subjectService.findSubject(id).lessonEntities.mapToLessonPojos()

    @GetMapping("/api/v1/lessons/{lessonId}/users")
    fun getUsersFromLesson(@PathVariable("lessonId") lessonId: Int) =
        lessonService.findLesson(lessonId).users

    @GetMapping("/api/v1/lessons")
    fun findAllLessons() = lessonService.findAllLessons().mapToLessonPojos()
}