package app.services

import app.data.repositories.UserRepository
import app.security.QueueUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class QueueUsersService : UserDetailsService  {

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String?): QueueUser =
        userRepository
            .findUserByUsername(username!!)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "No such user") }
            .run { QueueUser(this) }

    fun findUserById(id: Int) = userRepository
        .findById(id)
        .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "User with specified id not found") }
        .run { QueueUser(this) }

}