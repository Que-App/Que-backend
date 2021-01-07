package app.data.entities

import javax.persistence.*

@Entity
@Table(name = "exchanges")
class ExchangeEntity(
    @Id
    @GeneratedValue
    @Column(name = "exchange_id")
    var id: Int,

    var fromChangeId: Int,

    var toChangeId: Int,

    var requestId: Int,
)