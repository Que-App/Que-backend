package app.api.v1.pojos

import java.sql.Date
import java.sql.Time

class LessonPojo (
    var id: Int,

    var lessonindex: Int,

    var subjectid: Int,

    var nextdate: Date,

    var time: Time,

    var recurrenceinterval: Int,
)