package app.api.v1.pojos.mapping

import app.api.v1.pojos.RolePojo
import app.data.entities.RoleEntity
import java.util.*

fun RoleEntity.mapToPojo() = RolePojo(id, name, authorities)

fun Collection<RoleEntity>.mapToRolePojos() = map { it.mapToPojo() }

fun RolePojo.mapToEntity() = RoleEntity(id, name, authorities?: LinkedList())

fun Collection<RolePojo>.mapToRoleEntities() = map { it.mapToEntity() }