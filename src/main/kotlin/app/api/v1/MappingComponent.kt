package app.api.v1

import app.services.LessonService
import app.services.OccurrenceService
import app.services.SubjectService
import app.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MappingComponent {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var occurrenceService: OccurrenceService

    @Autowired
    lateinit var lessonService: LessonService

    @Autowired
    lateinit var subjectService: SubjectService
}