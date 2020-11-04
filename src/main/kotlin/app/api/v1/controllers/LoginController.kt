package app.api.v1.controllers

import app.api.v1.request.LoginRequest
import app.api.v1.response.AuthenticationFailedResponse
import app.api.v1.response.TokenResponse
import app.data.repositories.UserRepository
import app.security.JWTConfiguration
import app.security.QueueUser
import app.security.configure
import io.jsonwebtoken.Jwts
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController {

    companion object {
        private val log: Logger = LogManager.getLogger()
    }

    @Autowired
    private lateinit var  userRepository: UserRepository

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var jwtConf: JWTConfiguration


    @PostMapping("/api/v1/auth")
    fun authenticate(@RequestBody request: LoginRequest): ResponseEntity<Any> = try {
        val auth: Authentication =
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.username, request.password))

        val token = Jwts
            .builder()
            .configure(jwtConf)
            .setSubject((auth.principal as QueueUser).id.toString())
            .compact()

        ResponseEntity.ok(TokenResponse(token))
    }
    catch (e: DisabledException) {
        log.debug("User with username ${request.username} failed to obtain a token due to account being disabled")
        ResponseEntity.badRequest().body(AuthenticationFailedResponse("Account has been disabled. Please contact administrator"))
    }
    catch (e: LockedException) {
        log.debug("User with username ${request.username} failed to obtain a token due to account being locked")
        ResponseEntity.badRequest().body(AuthenticationFailedResponse("Account has been locked. Please contact administrator"))
    }
    catch (e: BadCredentialsException) {
        log.debug("Attempt of obtaining token with invalid credentials has been made for username ${request.username}")
        ResponseEntity.badRequest().body(AuthenticationFailedResponse("Invalid username or password."))
    }


}