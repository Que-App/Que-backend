package app.services.models

import java.time.LocalDateTime

class ExchangeRequestStatusChangeEvent(
    val time: LocalDateTime,

    val exchangeRequestId: Int,

    val newStatus: Status
) {
    enum class Status {
        PENDING,
        ACCEPTED,
        DECLINED,
        INVALID
    }
}