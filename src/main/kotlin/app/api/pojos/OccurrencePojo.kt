package app.api.pojos

import java.sql.Date
import java.util.*

class OccurrencePojo(
    val lessonid: UUID,

    val userid: UUID,

    val date: Date,

    val name: String,

    val surname: String,
)

