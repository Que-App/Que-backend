package app.data.repositories

import app.data.entities.SubjectEntity
import org.springframework.data.repository.CrudRepository

interface SubjectRepository : CrudRepository<SubjectEntity, Int>