package app.data.entities

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(

    @Id
    @GeneratedValue
    var id: Int,

    var username: String,

    var password: String,

    var enabled: Boolean,

)