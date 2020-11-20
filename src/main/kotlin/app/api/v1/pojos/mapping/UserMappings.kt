package app.api.v1.pojos.mapping

import app.api.v1.pojos.UserPojo
import app.data.entities.UserEntity

fun UserEntity.mapToPojo() = UserPojo(id, username)

fun Collection<UserEntity>.mapToUserPojos() = map { it.mapToPojo() }