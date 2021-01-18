package app.api.v1.controllers

import app.services.PermissionsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class PermissionsController {

    @Autowired
    private lateinit var permissionsService: PermissionsService

    @GetMapping("/api/v1/roles")
    fun getRoles() = permissionsService.findAllRoles()

    @GetMapping("/api/v1/authorities")
    fun getAllAuthorities() = permissionsService.findAllAuthorities()
}