package app.services

import app.api.v1.controllers.UserController
import app.api.v1.pojos.UserPojo
import app.api.v1.pojos.mapToPojo
import app.api.v1.response.AuthenticationFailedResponse
import app.data.entities.UserEntity
import app.data.repositories.UserRepository
import app.security.QueueUser
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import util.ok
import java.sql.SQLException
import java.sql.SQLIntegrityConstraintViolationException
import java.util.*

@Service
class UserService : UserDetailsService  {

    companion object {
        val log: Logger = LogManager.getLogger()
    }

    @Autowired
    private lateinit var encoder: PasswordEncoder

    @Autowired
    private lateinit var userRepository: UserRepository

    private val userNotFoundException
        get() = ResponseStatusException(HttpStatus.NOT_FOUND, "No such user")

    override fun loadUserByUsername(username: String?): QueueUser =
        userRepository
            .findUserByUsername(username!!)
            .orElseThrow { throw userNotFoundException }
            .run { QueueUser(this) }

    fun findUserDetailsById(id: Int) = userRepository
        .findById(id)
        .orElseThrow { throw userNotFoundException }
        .run { QueueUser(this) }

    fun findUserById(id: Int): UserPojo = userRepository
        .findById(id)
        .orElseThrow { throw userNotFoundException }
        .removeCredentials()
        .mapToPojo()

    fun saveUser(user: QueueUser) = userRepository.save(user.userEntity)

    fun saveUser(user: UserEntity) = userRepository.save(user)

    fun changePassword(oldPassword: String, newPassword: String): ResponseEntity<Any?> {
        val id: Int = (SecurityContextHolder.getContext().authentication.principal as QueueUser).id

        val user: QueueUser = findUserDetailsById(id)

        if(!encoder.matches(oldPassword, user.password)){
            log.debug("User with id $id attempted failed a password change due to wrong old password")
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(AuthenticationFailedResponse("Wrong password"))
        }

        user.userEntity.password = encoder.encode(newPassword)

        saveUser(user)

        log.debug("User with id $id has successfully changed his password")
        return ResponseEntity.ok().build()
    }

    fun createUser(username: String, password: String) = try {
        saveUser(UserEntity(0, username, encoder.encode(password), true, LinkedList()))
    }
    catch (e: DataIntegrityViolationException) {
        throw ResponseStatusException(HttpStatus.CONFLICT, "User with this username already exists.")
    }


    fun UserEntity.removeCredentials() = apply { password = "" }

}