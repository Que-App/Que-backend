package app.data.repositories

import app.data.entities.QueueEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface QueueRepository : CrudRepository<QueueEntity, UUID>