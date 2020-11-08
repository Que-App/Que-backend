package app.api.v1.controllers

import app.api.v1.request.AccountCreationRequest
import app.api.v1.request.ChangePasswordRequest
import app.api.v1.response.AuthenticationFailedResponse
import app.data.entities.UserEntity
import app.security.QueueUser
import app.services.UserService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import util.ok
import java.util.*

@RestController
class UserController {

    companion object {
        private val log: Logger = LogManager.getLogger()
    }

    @Autowired
    private lateinit var userService: UserService

    @PostMapping("/api/v1/account/changepass")
    fun changePassword(@RequestBody req: ChangePasswordRequest): ResponseEntity<Any?>
            = userService.changePassword(req.oldPassword, req.newPassword)

    @PostMapping("/api/v1/account/create")
    fun crateAccount(@RequestBody req: AccountCreationRequest) =
        userService.createUser(req.username, req.password).ok()
}