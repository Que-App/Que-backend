package app.api.v1.pojos

import java.sql.Date

class OccurrencePojo(
    val lessonid: Int?,

    val userid: Int?,

    val lessonindex: Int,

    val date: Date,

    val username: String,

)
