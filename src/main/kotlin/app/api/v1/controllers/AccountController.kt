package app.api.v1.controllers

import app.api.v1.request.AccountCreationRequest
import app.api.v1.request.ChangePasswordRequest
import app.api.v1.response.AuthenticationFailedResponse
import app.data.entities.UserEntity
import app.security.QueueUser
import app.services.QueueUsersService
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
class AccountController {

    companion object {
        private val log: Logger = LogManager.getLogger()
    }

    @Autowired
    private lateinit var encoder: PasswordEncoder

    @Autowired
    private lateinit var queueUsersService: QueueUsersService

    @PostMapping("/api/v1/account/changepass")
    fun changePassword(@RequestBody req: ChangePasswordRequest): ResponseEntity<Any?> {

        val id: Int = (SecurityContextHolder.getContext().authentication.principal as QueueUser).id

        val user: QueueUser = queueUsersService.findUserById(id)

        if(!encoder.matches(req.oldPassword, user.password)){
            log.debug("User with id $id attempted failed a password change due to wrong old password")
            return ResponseEntity.badRequest().body(AuthenticationFailedResponse("Wrong password"))
        }

        user.userEntity.password = encoder.encode(req.newPassword)

        queueUsersService.saveUser(user)

        log.debug("User with id $id has successfully changed his password")
        return ResponseEntity.ok().build()
    }

    @PostMapping("/api/v1/account/create")
    fun crateAccount(@RequestBody req: AccountCreationRequest) =
        queueUsersService.saveUser(UserEntity(0, req.username, encoder.encode(req.password), true, LinkedList()))
            .ok()
}