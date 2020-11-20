package app.api.v1.controllers

import app.api.v1.pojos.ExchangeRequestPojo
import app.api.v1.pojos.mapping.mapToEntity
import app.services.ExchangeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import util.ok
import javax.validation.Valid

@CrossOrigin
@RestController
class ExchangeController {

    @Autowired
    private lateinit var exchangeService: ExchangeService

    @GetMapping("/api/v1/exchanges")
    fun findAllExchanges() = exchangeService.findAllExchanges()

    @GetMapping("/api/v1/exchanges/{userId}") //TODO: Shouldn't the id be of currently authenticated user?
    fun findExchangesForUser(@PathVariable("userId") id: Int) =
        exchangeService.findExchangesForUser(id)

    @GetMapping("/api/v1/exchanges/requests/to")
    fun getExchangesForUser() = exchangeService.findRequestsToUser()

    @GetMapping("/api/v1/exchanges/requests/from")
    fun getExchangeRequestsByUser() = exchangeService.findRequestByUser()

    @PostMapping("/api/v1/exchanges/requests/submit")
    fun submitExchangeRequest(@Valid @RequestBody request: ExchangeRequestPojo) =
        exchangeService.saveRequest(request.mapToEntity())
            .ok()

    @PatchMapping("/api/v1/exchanges/requests/accept/{requestId}")
    fun acceptRequest(@PathVariable("requestId") id: Int) = exchangeService.acceptRequest(id)

    @PatchMapping("/api/v1/exchanges/requests/decline/{requestId}")
    fun declineRequest(@PathVariable("requestId") id: Int) = exchangeService.declineRequest(id)
}