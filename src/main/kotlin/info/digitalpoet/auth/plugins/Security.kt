package info.digitalpoet.auth.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import info.digitalpoet.auth.domain.entity.Token
import info.digitalpoet.auth.domain.model.AuthenticationScope
import io.ktor.application.Application
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTCredential
import io.ktor.auth.jwt.jwt

fun Application.configureSecurity()
{
    val jwtConfiguration = environment.config.config("jwt")

    authentication {
        jwt {

            val secret = jwtConfiguration.property("secret").getString()
            val issuer = jwtConfiguration.property("issuer").getString()
            val audience = jwtConfiguration.property("audience").getString()

            realm = jwtConfiguration.property("realm").getString()

            verifier {
                JWT
                    .require(Algorithm.HMAC512(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaimPresence("sub")
                    .withClaimPresence("iat")
                    .withClaimPresence("client")
                    .withClaimPresence("scope")
                    .acceptLeeway(2)
                    .build()
            }

            validate { credential ->
                if (credential.subject.isNullOrBlank()) null else mapCredentialToToken(credential)
            }
        }
    }
}

private fun mapCredentialToToken(credential: JWTCredential): Token
{
    val mapScope = credential.getClaim("scope", HashMap::class)!!

    val scope = mapScope
        .entries
        .map { AuthenticationScope(it.key as String, it.value as List<String>) }

    return Token(
        credential.subject!!,
        scope,
        credential["client"]!!,
        credential.expiresAt!!.toInstant()
    )
}
