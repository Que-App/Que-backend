package app.data.repositories

import app.data.entities.ExchangeEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface ExchangesRepository : CrudRepository<ExchangeEntity, Int> {

    @Query("SELECT * FROM exchanges WHERE from_user_id = :id OR to_user_id = :id", nativeQuery = true)
    fun findChangesForUser(id: Int): Iterable<ExchangeEntity>

}
