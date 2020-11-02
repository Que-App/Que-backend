package app.security

import app.services.QueueUsersService
import io.jsonwebtoken.Jwts
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticatonFilter(val queueUsersService: QueueUsersService, val jwtConf: JWTConfiguration) : OncePerRequestFilter() {
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token: String? = getToken(request)

        try {
            val userid: Int = Jwts
                .parserBuilder()
                .configure(jwtConf)
                .build()
                .parseClaimsJws(token)
                .body
                .subject
                .toInt()

            val user: QueueUser = queueUsersService.findUserById(userid).removeCredentials()

            val auth: UsernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(user, null, user.authorities)
            auth.details = WebAuthenticationDetailsSource().buildDetails(request)

            SecurityContextHolder.getContext().authentication = auth
        } catch (e: Exception) { }

        filterChain.doFilter(request, response)

    }

    private fun getToken(request: HttpServletRequest): String? {
        val header: String = request.getHeader(HttpHeaders.AUTHORIZATION)?: return null

        return if(header.startsWith("Bearer ")) header.substring(7, header.length) else null
    }
}