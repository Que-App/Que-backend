package app.security

import app.services.UserService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.beans.factory.annotation.Autowired
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
import java.security.Security

@EnableWebSecurity
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    companion object {
        private val log: Logger = LogManager.getLogger()
    }

    @Autowired
    private lateinit var jwtConf: JWTConfiguration

    @Autowired
    private lateinit var authEntryPoint: AuthEntryPointJwt

    @Autowired
    private lateinit var secureUsersService: UserService

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(secureUsersService)
        super.configure(auth)
    }

    override fun configure(http: HttpSecurity?) {
        if(http == null) return

        http.cors()
            .and()
            .csrf()
            .disable()

        http.addFilterBefore(JWTAuthenticationFilter(secureUsersService, jwtConf), UsernamePasswordAuthenticationFilter::class.java)

        http.exceptionHandling().authenticationEntryPoint(authEntryPoint)

        http.authorizeRequests()
            .antMatchers("/api/v1/auth").permitAll()
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