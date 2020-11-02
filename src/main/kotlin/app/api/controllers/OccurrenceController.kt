package app.api.controllers

import app.api.pojos.OccurrencePojo
import app.api.pojos.mapToOccurrencePojos
import app.services.OccurrenceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class OccurrenceController {

    @Autowired
    private lateinit var occurrenceService: OccurrenceService

    @RequestMapping("/occurrences/{lessonid}/{amount}")
    fun getOccurrences(@PathVariable("lessonid") lessonId: Int, @PathVariable("amount") amount: Int): List<OccurrencePojo> =
        occurrenceService.findPrevious(lessonId, amount).mapToOccurrencePojos()
}