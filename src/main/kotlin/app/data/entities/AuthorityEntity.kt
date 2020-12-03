package app.data.entities

import javax.persistence.*

@Entity
@Table(name = "authorities")
class AuthorityEntity (

    @Id
    @GeneratedValue
    @Column(name = "authority_id")
    var id: Int,

    var value: String

)