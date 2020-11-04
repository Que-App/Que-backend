package app.api.v1.controllers

import app.api.v1.request.ChangePasswordRequest
import app.api.v1.response.InvalidPasswordResponse
import app.security.QueueUser
import app.services.QueueUsersService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController {

    @Autowired
    private lateinit var encoder: PasswordEncoder

    @Autowired
    private lateinit var queueUsersService: QueueUsersService

    @PostMapping("/api/v1/account/changepass")
    fun changePassword(@RequestBody req: ChangePasswordRequest): ResponseEntity<Any?> {

        val id: Int = (SecurityContextHolder.getContext().authentication.principal as QueueUser).id

        val user: QueueUser = queueUsersService.findUserById(id)

        if(!encoder.matches(req.oldPassword, user.password))
            return ResponseEntity.badRequest().body(InvalidPasswordResponse())


        user.userEntity.password = encoder.encode(req.newPassword)

        queueUsersService.saveUser(user)

        return ResponseEntity.ok().build()
    }
}