package app.data.repositories

import app.data.entities.LessonEntity
import org.springframework.data.repository.CrudRepository

interface LessonRepository : CrudRepository<LessonEntity, Int>