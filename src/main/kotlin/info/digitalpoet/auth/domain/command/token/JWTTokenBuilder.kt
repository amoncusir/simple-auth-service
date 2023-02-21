package info.digitalpoet.auth.domain.command.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import info.digitalpoet.auth.domain.model.Authentication
import java.time.Instant
import java.util.*

class JWTTokenBuilder(
    private val configuration: Configuration
): TokenBuilder
{
    data class Configuration(
        val secret: String,
        val issuer: String,
        val audience: String,
        val ttl: Long,
    )

    override operator fun invoke(authentication: Authentication): TokenBuilder.Response
    {
        val now = Instant.now()

        val token = JWT.create()
            .withIssuer(configuration.issuer)
            .withAudience(*authentication.scope.map { it.service }.toTypedArray())
            .withSubject(authentication.user.userId)
            .withExpiresAt(Date.from(now.plusSeconds(configuration.ttl)))
            .withIssuedAt(Date.from(now))
            .withClaim("client", authentication.client)
            .withClaim("scope", authentication.scope.associate { it.service to it.grant })
            .sign(Algorithm.HMAC512(configuration.secret))

        return TokenBuilder.Response(token, authentication.refreshId)
    }
}
