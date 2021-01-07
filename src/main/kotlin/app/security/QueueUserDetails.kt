package app.security

import app.data.entities.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class QueueUserDetails(val userEntity: UserEntity) : UserDetails {

    val id: Int = userEntity.id

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = userEntity
        .roles
        .flatMap { it.authorities }
        .map { GrantedAuthority { it.value } }
        .toMutableList()

    override fun getPassword(): String = userEntity.password

    override fun getUsername(): String = userEntity.username

    override fun isEnabled(): Boolean = userEntity.enabled

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    fun removeCredentials(): QueueUserDetails {
        userEntity.password = ""
        return this
    }
}