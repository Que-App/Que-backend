package app.data.repositories

import app.data.entities.UserEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<UserEntity, Int> {

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1", nativeQuery = true)
    fun findUserByUsername(username: String): Optional<UserEntity>
}