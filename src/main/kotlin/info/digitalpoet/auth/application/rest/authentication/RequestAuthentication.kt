package info.digitalpoet.auth.application.rest.authentication

import info.digitalpoet.auth.domain.service.TokenService
import info.digitalpoet.auth.domain.service.UserAuthenticationService
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.application
import io.ktor.routing.get
import io.ktor.routing.post
import org.koin.ktor.ext.inject

data class BasicAuthentication(
    val email: String,
    val password: String,
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

    val tokenService by  inject<TokenService>()
    val userAuthenticationService by inject<UserAuthenticationService>()

    val ttl = application.environment.config.property("jwt.ttl").getString().toLong()

    get {
        val parameters = call.request.queryParameters

        val scope = parameters.getAll("scope")!!.associateWith { listOf("*") }

        val request = UserAuthenticationService.AuthenticationRequest(
            parameters["email"]!!,
            parameters["password"]!!,
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
