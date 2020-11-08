package app.security

import app.services.UserService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.security.SignatureException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticatonFilter(val secureUsersService: UserService, val jwtConf: JWTConfiguration) : OncePerRequestFilter() {

    companion object {
        private val log: Logger = LogManager.getLogger()
    }
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        getToken(request)?.let { authenticateWithToken(it, request) }

        filterChain.doFilter(request, response)

    }

    private fun authenticateWithToken(token: String, request: HttpServletRequest) = try {

        val userid: Int = Jwts
            .parserBuilder()
            .configure(jwtConf)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
            .toInt()

        val user: QueueUser = secureUsersService.findUserDetailsById(userid).removeCredentials()

        if(!user.isEnabled)
            throw DisabledException("User with id ${user.id} failed token authentication due to account being disabled")
        if(!user.isAccountNonLocked)
            throw LockedException("User with id ${user.id} failed token authentication due to account being locked")

        val auth = UsernamePasswordAuthenticationToken(user, null, user.authorities)
        auth.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = auth
    }

    catch (e: SignatureException) {
        log.warn("Token authentication failed due invalid signature")
    }
    catch (e: ExpiredJwtException) {
        log.debug("Token authentication failed due to expired token")
    }
    catch (e: JwtException) {
        log.debug("Token authentication failed due to exception: $e")
    }
    catch (e: LockedException) {
        log.debug(e.message)
    }
    catch (e: DisabledException) {
        log.debug(e.message)
    }

    private fun getToken(request: HttpServletRequest): String? {
        val header: String = request.getHeader(HttpHeaders.AUTHORIZATION)?: return null

        return if(header.startsWith("Bearer ")) header.substring(7, header.length) else null
    }
}