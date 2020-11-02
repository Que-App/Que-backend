package app.data.entities

import javax.persistence.*

@Entity
@Table(name = "users")
class UserEntity(

    @Id
    @GeneratedValue
    var id: Int,

    var username: String,

    var password: String,

    var enabled: Boolean,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "roles", joinColumns = [JoinColumn(name = "userid")])
    @Column(name = "role")
    var roles: Collection<String>

)