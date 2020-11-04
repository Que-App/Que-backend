package app.api.v1

import app.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MappingComponent {

    @Autowired
    lateinit var userService: UserService
}