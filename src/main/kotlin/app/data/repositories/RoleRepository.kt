package app.data.repositories

import app.data.entities.RoleEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface RoleRepository : CrudRepository<RoleEntity, Int> {

    @Query("INSERT INTO role_authorities VALUES(:roleId, :authorityId)", nativeQuery = true)
    fun addAuthorityToRole(roleId: Int, authorityId: Int)

    @Query("DELETE FROM role_authorities WHERE role_id = :roleId and authority_id = :authorityId", nativeQuery = true)
    fun removeAuthorityFromRole(roleId: Int, authorityId: Int)
}