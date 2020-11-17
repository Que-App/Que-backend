package app.data.repositories

import app.data.entities.ExchangeRequestEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface ExchangeRequestRepository : CrudRepository<ExchangeRequestEntity, Int> {


    @Query("SELECT * FROM exchange_requests r WHERE r.to_user_id = :id", nativeQuery = true)
    fun findRequestsToUser(id: Int): Iterable<ExchangeRequestEntity>

    @Query("SELECT * FROM exchange_requests r WHERE r.from_user_id = :id", nativeQuery = true)
    fun findRequestsByUser(id: Int): Iterable<ExchangeRequestEntity>

}