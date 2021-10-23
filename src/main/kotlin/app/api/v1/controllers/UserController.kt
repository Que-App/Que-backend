package app.api.v1.controllers

import app.api.v1.pojos.UserPojo
import app.api.v1.pojos.mapping.mapToPojo
import app.api.v1.pojos.mapping.mapToUserPojos
import app.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

    @Autowired
    private lateinit var userService: UserService

    @GetMapping("/api/v1/user/{userId}")
    fun findUserById(@PathVariable("userId") id: Int): UserPojo = userService.findUserById(id).mapToPojo()

    @GetMapping("/api/v1/users")
    fun findUsers(): List<UserPojo> = userService.findAllUsers().mapToUserPojos()
}