package app.api.v1.pojos.mapping

import app.api.v1.pojos.ExchangePojo
import app.data.entities.ExchangeEntity

fun ExchangeEntity.mapToPojo() = ExchangePojo(id, fromUserId, fromLessonId, fromDate, toUserId, toLessonId, toDate, acceptDate, fromChangeId, toChangeId)

fun Collection<ExchangeEntity>.mapToExchangePojos() = map { it.mapToPojo() }
