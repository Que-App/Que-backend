package app.security

import app.services.QueueUsersService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableWebSecurity
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Value("\${queue.api.auth.url}")
    lateinit var authUrl: String

    @Autowired
    lateinit var jwtConf: JWTConfiguration

    @Autowired
    lateinit var authEntryPoint: AuthEntryPointJwt

    @Autowired
    lateinit var queueUsersService: QueueUsersService

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(queueUsersService)
        super.configure(auth)
    }

    override fun configure(http: HttpSecurity?) {
        if(http == null) return

        http.cors()
            .and()
            .csrf()
            .disable()

        http.addFilterBefore(JWTAuthenticatonFilter(queueUsersService, jwtConf), UsernamePasswordAuthenticationFilter::class.java)

        http.exceptionHandling().authenticationEntryPoint(authEntryPoint)

        http.authorizeRequests()
            .antMatchers(authUrl).permitAll()
            .anyRequest().authenticated()

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }


    @Bean
    fun encoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
}