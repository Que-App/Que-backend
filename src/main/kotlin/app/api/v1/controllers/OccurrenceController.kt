package app.api.v1.controllers

import app.api.v1.MappingComponent
import app.api.v1.pojos.OccurrencePojo
import app.api.v1.pojos.mapToOccurrencePojos
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

    @Autowired
    private lateinit var mappingComponent: MappingComponent

    @RequestMapping("/api/v1/occurrences/{lessonid}/{amount}")
    fun getOccurrences(@PathVariable("lessonid") lessonId: Int, @PathVariable("amount") amount: Int): List<OccurrencePojo> =
        occurrenceService.findPrevious(lessonId, amount).mapToOccurrencePojos(mappingComponent)
}