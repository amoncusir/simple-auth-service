package info.digitalpoet.auth.plugins.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import info.digitalpoet.auth.domain.command.tracer.EventPublisher
import info.digitalpoet.auth.domain.entity.Token
import info.digitalpoet.auth.domain.model.AuthenticationScope
import info.digitalpoet.auth.domain.values.UserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.core.context.GlobalContext

fun tokenValidationForGrants(audience: String, vararg grants: String):
        suspend ApplicationCall.(JWTCredential) -> Principal?
{
    return validation@{credential ->
                if (!credential.subject.isNullOrEmpty()) {
                    val token = credential.toToken()
                    if (token.hasServiceWithGrants(audience, *grants)) token else null
                } else null
        }
}

fun AuthenticationConfig.jwtAuthentication(
    name: String,
    secret: String,
    issuer: String,
    audience: String,
    ownRealm: String,
    validateFun: suspend ApplicationCall.(JWTCredential) -> Principal?
)
{
    val eventPublisher by lazy { GlobalContext.get().get<EventPublisher>() }

    jwt(name) {

        realm = ownRealm

        verifier {
            JWT.require(Algorithm.HMAC512(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaimPresence("sub")
                .withClaimPresence("iat")
                .withClaimPresence("client")
                .withClaimPresence("scope")
                .acceptLeeway(2)
                .build()
        }

        validate {
            val token = validateFun(this, it) as Token?

            if (token != null) {
                eventPublisher("authentication.success", mapOf(
                    "accessType" to name,
                    "realm" to ownRealm,
                    "user" to token.userId,
                ))
            }

            token
        }

        challenge { defaultSchema, realm ->
            eventPublisher("authentication.fail", mapOf(
                "accessType" to name,
                "defaultSchema" to defaultSchema,
                "realm" to realm,
            ))

            call.respond(HttpStatusCode.Unauthorized, "Invalid Token")
        }
    }
}

internal fun JWTCredential.toToken(): Token
{
    val mapScope = getClaim("scope", HashMap::class)!!

    val scope = mapScope
        .entries
        .map { AuthenticationScope(it.key as String, it.value as List<String>) }

    return Token(
        UserId(subject!!),
        scope,
        this["client"]!!,
        expiresAt!!.toInstant()
    )
}