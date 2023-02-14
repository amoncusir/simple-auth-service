package info.digitalpoet.auth.application.rest.authentication

import info.digitalpoet.auth.domain.service.TokenService
import info.digitalpoet.auth.domain.service.UserAuthenticationService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

class BasicAuthentication(
    val email: String,
    val password: CharArray,
    val scope: Map<String, List<String>>,
    val refresh: Boolean = false,
)

fun BasicAuthentication.toDomain(clientId: String, ttl: Long): UserAuthenticationService.AuthenticationRequest {
    return UserAuthenticationService.AuthenticationRequest(
        email,
        password,
        scope,
        clientId,
        ttl,
        refresh
    )
}

fun Route.basicRequestAuthentication() {

    val tokenService by inject<TokenService>()
    val userAuthenticationService by inject<UserAuthenticationService>()

    val ttl = application.environment.config.property("jwt.ttl").getString().toLong()

    get {
        val parameters = call.request.queryParameters

        val scope = parameters.getAll("scope")!!.associateWith { listOf("*") }

        val request = UserAuthenticationService.AuthenticationRequest(
            parameters["email"]!!,
            parameters["password"]!!.toCharArray(),
            scope,
            call.parameters["clientId"]!!,
            ttl,
            parameters.contains("refresh"),
        )

        val authentication = userAuthenticationService.authenticateUser(request)
        val response = tokenService.buildToken(authentication)

        call.respond(hashMapOf("tokens" to response))
    }

    post {
        val basicAuth: BasicAuthentication = call.receive()
        val clientId = call.parameters["clientId"]!!

        val authentication = userAuthenticationService.authenticateUser(basicAuth.toDomain(clientId, ttl))
        val response = tokenService.buildToken(authentication)

        call.respond(hashMapOf("tokens" to response))
    }
}
