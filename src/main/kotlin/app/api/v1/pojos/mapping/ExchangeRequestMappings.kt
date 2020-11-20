package app.api.v1.pojos.mapping

import app.api.v1.pojos.ExchangeRequestPojo
import app.data.entities.ExchangeRequestEntity

fun ExchangeRequestEntity.mapToPojo() = ExchangeRequestPojo(id, fromUserId, fromLessonId, fromDate, toUserId, toLessonId, toDate)

fun Collection<ExchangeRequestEntity>.mapToExchangeRequestPojos() = map { it.mapToPojo() }

fun ExchangeRequestPojo.mapToEntity() = ExchangeRequestEntity(id!!, fromUserId!!, fromLessonId!!, fromDate!!, toUserId!!, toLessonId!!, toDate!!)

fun Collection<ExchangeRequestPojo>.mapToEntities() = map { it.mapToEntity() }

