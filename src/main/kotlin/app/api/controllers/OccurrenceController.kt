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
    lateinit var occurrenceService: OccurrenceService

    @RequestMapping("/occurrences/{amount}")
    fun getOccurrences(@PathVariable("amount") amount: Int): List<OccurrencePojo> =
        occurrenceService.findPrevious(amount).mapToOccurrencePojos()
}