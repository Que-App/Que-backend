package app.api.v1.pojos.mapping

import app.api.v1.pojos.AuthorityPojo
import app.data.entities.AuthorityEntity

fun AuthorityEntity.mapToPojo() = AuthorityPojo(id, value, description)

fun Collection<AuthorityEntity>.mapToAuthorityPojos() = map { it.mapToPojo() }

fun AuthorityPojo.mapToEntity() = AuthorityEntity(0, value!!, description!!)

fun Collection<AuthorityPojo>.mapToAuthorityEntities() = map { it.mapToEntity() }
