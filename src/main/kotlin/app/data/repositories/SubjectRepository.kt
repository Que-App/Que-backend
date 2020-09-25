package app.data.repositories

import app.data.entities.SubjectEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface SubjectRepository : CrudRepository<SubjectEntity, UUID>