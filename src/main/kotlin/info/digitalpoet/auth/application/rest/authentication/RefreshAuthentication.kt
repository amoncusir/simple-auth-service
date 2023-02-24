package info.digitalpoet.auth.application.rest.authentication

import info.digitalpoet.auth.domain.command.authentication.AuthenticationIssuer
import info.digitalpoet.auth.domain.command.token.TokenBuilder
import info.digitalpoet.auth.domain.values.RefreshId
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.refreshToken() {

    val tokenBuilder by  inject<TokenBuilder>()
    val authenticationIssuer by inject<AuthenticationIssuer>()

    get("/authentication/refresh/{refreshId}") {

        val refreshId = call.parameters["refreshId"]!!

        val authentication = authenticationIssuer(RefreshId(refreshId))
        val response = tokenBuilder(authentication)

        call.respond(mapOf("tokens" to response.toResponse()))
    }
}
