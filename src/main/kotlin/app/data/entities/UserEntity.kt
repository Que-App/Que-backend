package app.data.entities

import javax.persistence.*

@Entity
@Table(name = "users")
class UserEntity(

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    var id: Int,

    var username: String,

    var password: String,

    var enabled: Boolean,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "roles", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "role")
    var roles: Collection<String>

)