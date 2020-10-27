package app.services

import app.api.pojos.UserPojo
import app.api.pojos.mapToPojo
import app.data.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    fun findUser(id: Int): UserPojo =
        userRepository.findById(id)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "User with specified id was not found") }
            .mapToPojo()
}