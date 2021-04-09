package app.services

import app.api.v1.pojos.UserPojo
import app.api.v1.pojos.mapping.mapToPojo
import app.data.entities.UserEntity
import app.data.repositories.UserRepository
import app.security.QueueUserDetails
import app.services.exceptions.EntityNotFoundException
import app.services.exceptions.UnauthorizedException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
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

    val currentlyAuthenticatedUserDetails: QueueUserDetails
        get() = SecurityContextHolder.getContext().authentication.principal as QueueUserDetails

    private val userNotFoundException
        get() = EntityNotFoundException("user")

    override fun loadUserByUsername(username: String?): QueueUserDetails =
        userRepository
            .findUserByUsername(username!!)
            .orElseThrow { throw UsernameNotFoundException("User with username $username was not found.") }
            .run { QueueUserDetails(this) }

    fun findUserDetailsById(id: Int) = userRepository
        .findById(id)
        .orElseThrow { throw userNotFoundException }
        .run { QueueUserDetails(this) }

    fun findUserById(id: Int): UserEntity = userRepository
        .findById(id)
        .orElseThrow { throw userNotFoundException }
        .removeCredentials()

    fun saveUser(userDetails: QueueUserDetails) = userRepository.save(userDetails.userEntity)

    fun saveUser(user: UserEntity) = userRepository.save(user)

    fun changePassword(oldPassword: String, newPassword: String): ResponseEntity<Any?> {
        val id: Int = (SecurityContextHolder.getContext().authentication.principal as QueueUserDetails).id

        val userDetails: QueueUserDetails = findUserDetailsById(id)

        if(!encoder.matches(oldPassword, userDetails.password)){
            log.debug("User with id $id attempted failed a password change due to wrong old password")
            throw UnauthorizedException("Invalid old password")
        }

        userDetails.userEntity.password = encoder.encode(newPassword)

        saveUser(userDetails)

        log.debug("User with id $id has successfully changed his password")
        return ResponseEntity.ok().build()
    }

    fun createUser(username: String, password: String) = try {
        saveUser(UserEntity(0, username, encoder.encode(password), true, LinkedList()))
    }
    catch (e: DataIntegrityViolationException) {
        //TODO: Remove later when error handling is more complete
        throw ResponseStatusException(HttpStatus.CONFLICT, "User with this username already exists.")
    }

    fun isNowAuthenticated(id: Int): Boolean = currentlyAuthenticatedUserDetails.id == id


    fun UserEntity.removeCredentials() = apply { password = "" }

    // TODO: Remove. This is only for demo purposes
    fun findAllUsers(): List<UserPojo> = userRepository.findAll().map { it.mapToPojo() }

}