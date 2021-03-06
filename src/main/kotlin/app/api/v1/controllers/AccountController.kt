package app.api.v1.controllers

import app.api.v1.request.ChangePasswordRequest
import app.services.UserService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import util.ok
import javax.validation.Valid

@CrossOrigin
@RestController
class AccountController {

    companion object {
        private val log: Logger = LogManager.getLogger()
    }

    @Autowired
    private lateinit var userService: UserService

    @PostMapping("/api/v1/account/changepass")
    fun changePassword(@Valid @RequestBody req: ChangePasswordRequest): ResponseEntity<Any?>
            = userService.changePassword(req.oldPassword!!, req.newPassword!!).ok()
}