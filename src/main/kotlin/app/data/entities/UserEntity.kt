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
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id"
        )],
        inverseJoinColumns = [JoinColumn(
            name = "role_id",
            referencedColumnName = "role_id"
        )]
    )
    var roles: Collection<RoleEntity>

)