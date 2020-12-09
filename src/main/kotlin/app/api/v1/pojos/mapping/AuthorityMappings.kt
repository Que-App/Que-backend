package app.api.v1.pojos.mapping

import app.api.v1.pojos.AuthorityPojo
import app.data.entities.AuthorityEntity

fun AuthorityEntity.mapToPojo() = AuthorityPojo(id, value)

fun Collection<AuthorityEntity>.mapToAuthorityPojos() = map { it.mapToPojo() }

fun AuthorityPojo.mapToEntity() = AuthorityEntity(id, value!!)

fun Collection<AuthorityPojo>.mapToAuthorityEntities() = map { it.mapToEntity() }
