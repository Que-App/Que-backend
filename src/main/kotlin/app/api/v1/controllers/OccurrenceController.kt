package app.api.v1.controllers

import app.api.v1.MappingComponent
import app.api.v1.pojos.OccurrencePojo
import app.api.v1.pojos.mapping.mapToOccurrencePojos
import app.services.OccurrenceService
import app.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.Max

@RestController
@Validated
class OccurrenceController {

    @Autowired
    private lateinit var occurrenceService: OccurrenceService

    @Autowired
    private lateinit var mappingComponent: MappingComponent

    @Autowired
    private lateinit var userService: UserService

    @GetMapping("/api/v1/occurrences/update")
    fun updateOccurrences() = occurrenceService.updateOccurrences()

    @GetMapping("/api/v1/occurrences/past/{lessonId}/{amount}")
    fun getPreviousForLesson(
        @PathVariable("lessonId") lessonId: Int,
        @PathVariable("amount") @Max(30, message = "The maximum amount of occurrences is 30") amount: Int
    ): List<OccurrencePojo> =
        occurrenceService.findPreviousForLesson(lessonId, amount).mapToOccurrencePojos(mappingComponent)

    @GetMapping("/api/v1/occurrences/past/{amount}")
    fun getPrevious(
        @PathVariable("amount") @Max(30, message = "The maximum amount of occurrences is 30") amount: Int
    ) =
        occurrenceService.findPrevious(amount).mapToOccurrencePojos(mappingComponent)


    @GetMapping("/api/v1/occurrences/next/{lessonId}/{amount}")
    fun getQueue(
        @PathVariable lessonId: Int,
        @PathVariable("amount") @Max(30, message = "The maximum amount of occurrences is 30") amount: Int
    ): List<OccurrencePojo> =
        occurrenceService
            .peekOccurrences(lessonId)
            .asSequence()
            .take(amount)
            .toList()
            .mapToOccurrencePojos(mappingComponent)

}