package app.security

import app.data.entities.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class QueueUser(private val userEntity: UserEntity) : UserDetails {

    val id: Int = userEntity.id

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = userEntity.roles.map { GrantedAuthority { it } }.toMutableList()

    override fun getPassword(): String = userEntity.password

    override fun getUsername(): String = userEntity.username

    override fun isEnabled(): Boolean = userEntity.enabled

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    fun removeCredentials(): QueueUser {
        userEntity.password = "PROTECTED"
        return this
    }
}