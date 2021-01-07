package app.api.v1.pojos.mapping

import app.api.v1.pojos.ExchangeRequestPojo
import app.data.entities.ExchangeRequestEntity

fun ExchangeRequestEntity.mapToPojo() = ExchangeRequestPojo(
    id, fromUserId, fromLessonId, fromDate, toUserId, toLessonId, toDate, status, resolvementDate
)

fun Collection<ExchangeRequestEntity>.mapToExchangeRequestPojos() = map { it.mapToPojo() }

fun ExchangeRequestPojo.mapToEntity() = ExchangeRequestEntity(
    0, fromUserId!!, fromLessonId!!, fromDate!!, toUserId!!, toLessonId!!, toDate!!,
    status?: ExchangeRequestEntity.Status.PENDING, resolvementDate,
)

fun Collection<ExchangeRequestPojo>.mapToEntities() = map { it.mapToEntity() }

