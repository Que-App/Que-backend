package app.api.controllers

import app.api.request.LoginRequest
import app.api.response.JwtResponse
import app.data.repositories.UserRepository
import app.security.JWTConfiguration
import app.security.QueueUser
import app.security.configure
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController {

    @Autowired
    private lateinit var  userRepository: UserRepository

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var jwtConf: JWTConfiguration


    @PostMapping("/auth")
    fun authenticate(@RequestBody request: LoginRequest): ResponseEntity<JwtResponse> {

        val auth: Authentication =
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.username, request.password))

        val token = Jwts
            .builder()
            .configure(jwtConf)
            .setSubject((auth.principal as QueueUser).id.toString())
            .compact()

        return ResponseEntity.ok(JwtResponse(token))

    }


}