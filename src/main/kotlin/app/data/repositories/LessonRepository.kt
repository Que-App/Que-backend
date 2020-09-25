package app.data.repositories

import app.data.entities.LessonEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface LessonRepository : CrudRepository<LessonEntity, UUID>