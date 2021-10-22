package app.api.v1.controllers

import app.api.v1.MappingComponent
import app.api.v1.pojos.ExchangeRequestPojo
import app.api.v1.pojos.mapping.mapToEntity
import app.api.v1.pojos.mapping.mapToExchangeRequestPojos
import app.api.v1.validation.MaxIndex
import app.services.ExchangeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import util.ok
import javax.validation.Valid

@RestController
@Validated
class ExchangeController {

    @Autowired
    private lateinit var exchangeService: ExchangeService

    @Autowired
    private lateinit var mappingComponent: MappingComponent

    @GetMapping("/api/v1/exchanges/requests/{requestId}")
    fun getRequestById(@PathVariable("requestId") id: Int) = exchangeService.findRequestById(id)

    @GetMapping("/api/v1/exchanges/requests/to")
    fun getExchangeRequestsToUser() = exchangeService.findRequestsToUser().mapToExchangeRequestPojos(mappingComponent)

    @GetMapping("/api/v1/exchanges/requests/from")
    fun getExchangeRequestsByUser() = exchangeService.findRequestByUser().mapToExchangeRequestPojos(mappingComponent)

    @PostMapping("/api/v1/exchanges/requests/submit")
    fun submitExchangeRequest(@Valid @RequestBody @MaxIndex request: ExchangeRequestPojo) =
        exchangeService.saveRequest(request.mapToEntity())
            .ok()

    @GetMapping("/api/v1/exchanges/requests/accept/{requestId}")
    fun acceptRequest(@PathVariable("requestId") id: Int) = exchangeService.acceptRequest(id)

    @GetMapping("/api/v1/exchanges/requests/decline/{requestId}")
    fun declineRequest(@PathVariable("requestId") id: Int) = exchangeService.declineRequest(id)
}