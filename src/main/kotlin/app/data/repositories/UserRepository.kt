package app.data.repositories

import app.data.entities.UserEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<UserEntity, UUID>