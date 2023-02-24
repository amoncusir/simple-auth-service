package info.digitalpoet.auth.application.rest.authentication

import info.digitalpoet.auth.domain.command.authentication.AuthenticationIssuer
import info.digitalpoet.auth.domain.command.token.TokenBuilder
import info.digitalpoet.auth.plugins.SerializableCharArray
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
class BasicAuthentication(
    val email: String,
    val password: SerializableCharArray,
    val scope: Map<String, List<String>>,
    val refresh: Boolean = false,
)

fun BasicAuthentication.toDomain(clientId: String, ttl: Long) =
    AuthenticationIssuer.Request(
        email,
        password,
        scope,
        clientId,
        ttl,
        refresh
    )

fun Route.basicRequestAuthentication() {

    val tokenBuilder by inject<TokenBuilder>()
    val authenticationIssuer by inject<AuthenticationIssuer>()

    val ttl = application.environment.config.property("jwt.ttl").getString().toLong()

    route("/authentication/{clientId}/basic") {
        get {
            val parameters = call.request.queryParameters

            val scope = parameters.getAll("scope")!!.associateWith { listOf<String>() }

            val request = AuthenticationIssuer.Request(
                parameters["email"]!!,
                parameters["password"]!!.toCharArray(),
                scope,
                call.parameters["clientId"]!!,
                ttl,
                parameters.contains("refresh"),
            )

            val authentication = authenticationIssuer(request)
            val response = tokenBuilder(authentication)

            call.respond(mapOf("tokens" to response.toResponse()))
        }

        post {
            val basicAuth: BasicAuthentication = call.receive()
            val clientId = call.parameters["clientId"]!!

            val authentication = authenticationIssuer(basicAuth.toDomain(clientId, ttl))
            val response = tokenBuilder(authentication)

            call.respond(hashMapOf("tokens" to response.toResponse()))
        }
    }
}
