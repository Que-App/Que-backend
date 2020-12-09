package app.api.v1.controllers

import app.api.v1.pojos.RolePojo
import app.api.v1.pojos.mapping.mapToEntity
import app.services.RoleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import util.ok
import javax.validation.Valid

@CrossOrigin
@RestController
class RoleController {

    @Autowired
    private lateinit var roleService: RoleService

    @GetMapping("/api/v1/roles")
    fun getRoles() = roleService.findAllRoles()

    @PostMapping("/api/v1/roles")
    fun crateRole(@Valid @RequestBody role: RolePojo) =
        roleService.saveRole(role.mapToEntity())
            .ok()

    @DeleteMapping("api/v1/roles/{roleId}")
    fun deleteRole(@PathVariable("roleId") roleId: Int) =
        roleService.deleteRoleById(roleId)

    @PostMapping("/api/v1/roles/{roleId}/authorities/{authorityId}")
    fun addAuthorityToRole(@PathVariable roleId: Int, @PathVariable authorityId: Int) =
        roleService.addAuthorityToRole(roleId, authorityId)

    @DeleteMapping("/api/v1/roles/{roleId}/authorities/{authorityId}")
    fun removeAuthorityFromRole(@PathVariable roleId: Int, @PathVariable authorityId: Int) =
        roleService.removeAuthorityFromRole(roleId, authorityId)
}