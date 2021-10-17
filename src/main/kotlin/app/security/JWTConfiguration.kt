package app.security

import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.JwtParserBuilder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.annotation.PostConstruct

@Component
class JWTConfiguration {

    companion object {
        const val DEFAULT_VALIDITY: Long = 900000

        private val log: Logger = LogManager.getLogger()
    }

    @Value("\${queue.api.auth.token.key.private.pkcs8}")
    private lateinit var privateKeyValue: String

    final lateinit var privateKey: PrivateKey
        private set

    @Value("\${queue.api.auth.token.key.public.x509}")
    private lateinit var publicKeyValue: String

    final lateinit var publicKey: PublicKey
        private set

    @Value("\${queue.api.auth.token.issuer:}")
    lateinit var issuer: String

    @Value("\${queue.api.auth.token.audience:}")
    lateinit var audience: String

    @Value("\${queue.api.auth.token.validity:}")
    private lateinit var validityValue: String

    final var validity: Long = DEFAULT_VALIDITY
        private set


    @PostConstruct
    private fun init() {
        if(audience.isBlank())
            throw IllegalStateException("Configuration error: queue.api.auth.token.audience is not set")
        if(issuer.isBlank())
            throw IllegalStateException("Configuration error: queue.api.auth.token.issuer is not set")

        if(validityValue.isNotEmpty()) try {
            validity = validityValue.toLong()
        } catch (ignored: Exception) {
            log.warn("Invalid token validity provided, falling back to default: $DEFAULT_VALIDITY")
        }

        val keyFactory  = KeyFactory.getInstance("EC")

        val privateKeyPKCS8 = Base64.getDecoder().decode(privateKeyValue)
        val privateKeySpec = PKCS8EncodedKeySpec(privateKeyPKCS8)
        privateKey = keyFactory.generatePrivate(privateKeySpec)

        val publicKeyX509 = Base64.getDecoder().decode(publicKeyValue)
        val publicKeySpec = X509EncodedKeySpec(publicKeyX509)
        publicKey = keyFactory.generatePublic(publicKeySpec)
    }

}

fun JwtBuilder.configure(conf: JWTConfiguration): JwtBuilder {
    setIssuer(conf.issuer)
    setAudience(conf.audience)
    setExpiration(Date(Date().time + conf.validity))
    signWith(conf.privateKey)
    return this
}

fun JwtParserBuilder.configure(conf: JWTConfiguration): JwtParserBuilder {
    requireIssuer(conf.issuer)
    requireAudience(conf.audience)
    setSigningKey(conf.publicKey)
    return this
}

