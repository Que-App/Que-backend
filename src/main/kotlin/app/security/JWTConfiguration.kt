package app.security

import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.JwtParserBuilder
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JWTConfiguration {

    companion object {
        const val DEFAULT_VALIDITY: Long = 900000
    }

    @Value("\${queue.api.auth.token.secret:}")
    private lateinit var secretValue: String

    private var _key: SecretKey? = null

    val key: SecretKey
        get() {
            if(_key == null) initKey()
            return _key!!
        }

    @Value("\${queue.api.auth.token.issuer}")
    lateinit var issuer: String

    @Value("\${queue.api.auth.token.audience}")
    lateinit var audience: String

    @Value("\${queue.api.auth.token.validity:}")
    private lateinit var validityValue: String

    private var _validity: Long? = null

    val validity: Long
        get() {
            if(_validity == null) initValidity()
            return _validity!!
        }

    private fun initKey() =
        if (secretValue.isEmpty()) _key = Keys.secretKeyFor(SignatureAlgorithm.HS512)
        else _key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretValue))

    private fun initValidity() {
        if(validityValue.isNotEmpty()) try {
            _validity = validityValue.toLong()
            return
        } catch (ignored: Exception) {}

        _validity = DEFAULT_VALIDITY
    }

}

fun JwtBuilder.configure(conf: JWTConfiguration): JwtBuilder {
    setIssuer(conf.issuer)
    setAudience(conf.audience)
    setExpiration(Date(Date().time + conf.validity))
    signWith(conf.key)
    return this
}

fun JwtParserBuilder.configure(conf: JWTConfiguration): JwtParserBuilder {
    requireIssuer(conf.issuer)
    requireAudience(conf.audience)
    setSigningKey(conf.key)
    return this
}

