package app.services

import app.data.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    fun getUserData(userid: UUID): Pair<String, String> =
        userRepository.findById(userid)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found") }
            .run { Pair(name, surname) }
}