package app.data.entities

import javax.persistence.*

@Entity
@Table(name = "roles")
class RoleEntity (

    @Id
    @GeneratedValue
    @Column(name = "role_id")
    var id: Int,

    var name: String,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_authorities",
        joinColumns = [JoinColumn(
            name = "role_id",
            referencedColumnName = "role_id"
        )],
        inverseJoinColumns = [JoinColumn(
            name = "authority_id",
            referencedColumnName = "authority_id"
        )]

    )
    var authorities: List<AuthorityEntity>,
)