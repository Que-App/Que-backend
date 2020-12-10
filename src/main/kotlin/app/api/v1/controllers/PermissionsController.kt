package app.api.v1.controllers

import app.api.v1.pojos.RolePojo
import app.api.v1.pojos.mapping.mapToEntity
import app.services.PermissionsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import util.ok
import javax.validation.Valid

@CrossOrigin
@RestController
class PermissionsController {

    @Autowired
    private lateinit var permissionsService: PermissionsService

    @GetMapping("/api/v1/roles")
    fun getRoles() = permissionsService.findAllRoles()

    @PostMapping("/api/v1/roles")
    fun crateRole(@Valid @RequestBody role: RolePojo) =
        permissionsService.saveRole(role.mapToEntity())
            .ok()

    @DeleteMapping("api/v1/roles/{roleId}")
    fun deleteRole(@PathVariable("roleId") roleId: Int) =
        permissionsService.deleteRoleById(roleId)

    @PostMapping("/api/v1/roles/{roleId}/authorities/{authorityId}")
    fun addAuthorityToRole(@PathVariable roleId: Int, @PathVariable authorityId: Int) =
        permissionsService.addAuthorityToRole(roleId, authorityId)

    @DeleteMapping("/api/v1/roles/{roleId}/authorities/{authorityId}")
    fun removeAuthorityFromRole(@PathVariable roleId: Int, @PathVariable authorityId: Int) =
        permissionsService.removeAuthorityFromRole(roleId, authorityId)

    @GetMapping("/api/v1/authorities")
    fun getAllAuthorities() = permissionsService.findAllAuthorities()
}