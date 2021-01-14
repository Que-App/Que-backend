package app.api.v1.pojos.mapping

import app.api.v1.pojos.ExchangeRequestPojo
import app.data.entities.ExchangeRequestEntity

fun ExchangeRequestEntity.mapToPojo() = ExchangeRequestPojo(
    id, fromUserId, fromLessonId, fromIndex, toUserId, toLessonId, toIndex, status, resolvementTime
)

fun Collection<ExchangeRequestEntity>.mapToExchangeRequestPojos() = map { it.mapToPojo() }

fun ExchangeRequestPojo.mapToEntity() = ExchangeRequestEntity(
    0, fromUserId!!, fromLessonId!!, fromIndex!!, toUserId!!, toLessonId!!, toIndex!!,
    status?: ExchangeRequestEntity.Status.PENDING, resolvementTime,
)

fun Collection<ExchangeRequestPojo>.mapToEntities() = map { it.mapToEntity() }

