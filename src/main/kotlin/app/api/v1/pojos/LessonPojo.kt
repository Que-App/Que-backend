package app.api.v1.pojos

import app.api.v1.validation.FutureDate
import java.sql.Date
import java.sql.Time
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

class LessonPojo (

    @field:Min(0, message = "Id cannot be negative")
    var id: Int?,

    @field:Min(0, message = "Lesson index cannot be negative")
    var lessonIndex: Int?,

    @field:NotNull(message = "Subject id must be specified")
    @field:Min(0, message = "SubjectId cannot be negative")
    var subjectId: Int?,

    @field:NotNull(message = "Next date must be specified")
    @field:FutureDate
    var nextDate: Date?,

    @field:NotNull(message = "Time must be specified")
    var time: Time?,

    @field:NotNull(message = "Recurrence interval must be specified")
    @field:Min(1, message = "Recurrence interval must be at least 1")
    var recurrenceInterval: Int?,
)