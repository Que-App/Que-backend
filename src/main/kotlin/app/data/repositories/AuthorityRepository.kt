package app.data.repositories

import app.data.entities.AuthorityEntity
import org.springframework.data.repository.CrudRepository

interface AuthorityRepository : CrudRepository<AuthorityEntity, Int>  {

}