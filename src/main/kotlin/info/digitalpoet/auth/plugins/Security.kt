package info.digitalpoet.auth.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import info.digitalpoet.auth.domain.entity.Token
import info.digitalpoet.auth.domain.model.AuthenticationScope
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.hsts.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
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

            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Invalid Token")
            }
        }
    }

    install(HSTS) {
        includeSubDomains = true
    }
}

private fun mapCredentialToToken(credential: JWTCredential): Token {
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
