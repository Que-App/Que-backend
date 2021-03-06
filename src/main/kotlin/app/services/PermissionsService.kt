package app.services

import app.data.entities.RoleEntity
import app.data.repositories.AuthorityRepository
import app.data.repositories.RoleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PermissionsService {

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var authorityRepository: AuthorityRepository

    fun findAllRoles() = roleRepository.findAll()

    fun findRoleById(id: Int) = roleRepository.findById(id)

    fun saveRole(role: RoleEntity) = roleRepository.save(role)

    fun deleteRole(role: RoleEntity) = roleRepository.delete(role)

    fun deleteRoleById(roleId: Int) = roleRepository.deleteById(roleId)

    fun addAuthorityToRole(roleId: Int, authorityId: Int) =
        roleRepository.addAuthorityToRole(roleId, authorityId)

    fun removeAuthorityFromRole(roleId: Int, authorityId: Int) =
        roleRepository.removeAuthorityFromRole(roleId, authorityId)

    fun findAllAuthorities() = authorityRepository.findAll()
}