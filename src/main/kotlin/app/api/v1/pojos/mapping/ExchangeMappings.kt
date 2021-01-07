package app.api.v1.pojos.mapping

import app.api.v1.pojos.ExchangePojo
import app.data.entities.ExchangeEntity

fun ExchangeEntity.mapToPojo() = ExchangePojo(id, fromChangeId, toChangeId, requestId)

fun Collection<ExchangeEntity>.mapToExchangePojos() = map { it.mapToPojo() }
