package app.data.entities

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(

    @Id
    @GeneratedValue(generator = "UUID")
    @Type(type = "uuid-char")
    var id: UUID,

    var name: String,

    var surname: String
)