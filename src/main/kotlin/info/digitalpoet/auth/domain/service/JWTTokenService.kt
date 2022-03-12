package info.digitalpoet.auth.domain.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import info.digitalpoet.auth.domain.model.Authentication
import java.time.Instant
import java.util.Date

class JWTTokenService(
    private val configuration: Configuration
): TokenService
{
    data class Configuration(
        val secret: String,
        val issuer: String,
        val audience: String,
        val ttl: Long,
    )

    override fun buildToken(authentication: Authentication): TokenService.TokenResponse
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

        return TokenService.TokenResponse(token, authentication.refreshId)
    }
}
