package info.digitalpoet.auth.plugins

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
import io.ktor.server.plugins.hsts.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.context.GlobalContext
import org.koin.java.KoinJavaComponent

fun Route.authenticateSelf(build: Route.() -> Unit) = authenticate(
    "self", strategy = AuthenticationStrategy.Required, build = build
)

fun Route.authenticateAdmin(build: Route.() -> Unit) = authenticate(
    "admin", strategy = AuthenticationStrategy.Required, build = build
)

fun Route.authenticateService(build: Route.() -> Unit) = authenticate(
    "service", strategy = AuthenticationStrategy.Required, build = build
)

fun Application.configureSecurity() {
    val jwtConfiguration = environment.config.config("jwt")

    authentication {

        val issuer = jwtConfiguration.property("issuer").getString()
        val audience = jwtConfiguration.property("audience").getString()
        val ownRealm = jwtConfiguration.property("realm").getString()

        jwtAuthentication(
            name = "self",
            secret = jwtConfiguration.property("secret").getString(),
            issuer = issuer,
            audience = audience,
            ownRealm = ownRealm,
            validateFun = tokenValidationForGrants(audience, "self")
        )

        jwtAuthentication(
            name = "admin",
            secret = jwtConfiguration.property("secret").getString(),
            issuer = issuer,
            audience = audience,
            ownRealm = ownRealm,
            validateFun = tokenValidationForGrants(audience, "admin")
        )

        jwtAuthentication(
            name = "service",
            secret = jwtConfiguration.property("service-secret").getString(),
            issuer = issuer,
            audience = audience,
            ownRealm = ownRealm,
            validateFun = { jwtCredential ->
                if(jwtCredential.subject.isNullOrEmpty()) null else mapCredentialToToken(jwtCredential)
            }
        )
    }

    install(HSTS) {
        includeSubDomains = true
    }
}

fun tokenValidationForGrants(audience: String, vararg grants: String): suspend ApplicationCall.(JWTCredential) -> Principal?
{
    return validation@{credential ->
                if (!credential.subject.isNullOrEmpty()) {
                    val token = mapCredentialToToken(credential)
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
    val eventPublisher by GlobalContext.get().inject<EventPublisher>()

    jwt(name) {

        realm = ownRealm

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

        validate(validateFun)

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

private fun mapCredentialToToken(credential: JWTCredential): Token {
    val mapScope = credential.getClaim("scope", HashMap::class)!!

    val scope = mapScope
        .entries
        .map { AuthenticationScope(it.key as String, it.value as List<String>) }

    return Token(
        UserId(credential.subject!!),
        scope,
        credential["client"]!!,
        credential.expiresAt!!.toInstant()
    )
}
