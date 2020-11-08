package app.api.v1.controllers

import app.api.v1.MappingComponent
import app.api.v1.pojos.OccurrencePojo
import app.api.v1.pojos.mapToOccurrencePojos
import app.api.v1.response.BadRequestResponse
import app.services.OccurrenceService
import app.services.UserService
import engine.exceptions.EmptyQueueException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class OccurrenceController {

    @Autowired
    private lateinit var occurrenceService: OccurrenceService

    @Autowired
    private lateinit var mappingComponent: MappingComponent

    @Autowired
    private lateinit var userService: UserService

    @GetMapping("/api/v1/occurrences/past/{lessonid}/{amount}")
    fun getOccurrences(@PathVariable("lessonid") lessonId: Int, @PathVariable("amount") amount: Int): List<OccurrencePojo> =
        occurrenceService.findPrevious(lessonId, amount).mapToOccurrencePojos(mappingComponent)

    @GetMapping("/api/v1/occurrences/next/{lessonid}/{amount}")
    fun getQueue(@PathVariable lessonid: Int, @PathVariable("amount") amount: Int) = try {
        occurrenceService.commitPast(lessonid)

        occurrenceService.peekOccurrence(lessonid, amount).map {
            val userPojo = userService.findUserById(it.userid!!)
            OccurrencePojo(it.lessonid, it.userid, it.lessonindex, it.date, userPojo.username)
        }
    }
    catch (e: EmptyQueueException) {
        ResponseEntity.badRequest().body(
            BadRequestResponse.Builder()
                .cause("There are no users in this queue.")
                .action("Add at least one user to this queue.")
                .build()
        )
    }
}