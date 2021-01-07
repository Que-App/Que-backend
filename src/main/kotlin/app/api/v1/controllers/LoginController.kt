package app.api.v1.controllers

import app.api.v1.request.LoginRequest
import app.api.v1.response.TokenResponse
import app.security.JWTConfiguration
import app.security.QueueUserDetails
import app.security.configure
import io.jsonwebtoken.Jwts
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@CrossOrigin
@RestController
class LoginController {

    companion object {
        private val log: Logger = LogManager.getLogger()
    }

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var jwtConf: JWTConfiguration


    @PostMapping("/api/v1/auth")
    fun authenticate(@Valid @RequestBody request: LoginRequest): ResponseEntity<Any> = try {
        val auth: Authentication =
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.username, request.password))

        val token = Jwts
            .builder()
            .configure(jwtConf)
            .setSubject((auth.principal as QueueUserDetails).id.toString())
            .compact()

        ResponseEntity.ok(TokenResponse(token))
    }
    catch (e: DisabledException) {
        log.debug("User with username ${request.username} failed to obtain a token due to account being disabled")
        throw e
    }
    catch (e: LockedException) {
        log.debug("User with username ${request.username} failed to obtain a token due to account being locked")
        throw e
    }
    catch (e: BadCredentialsException) {
        log.debug("Attempt of obtaining token with invalid credentials has been made for username ${request.username}")
        throw e
    }

}