package info.digitalpoet.auth.application.rest.authentication

import info.digitalpoet.auth.domain.cases.token.TokenBuilder
import info.digitalpoet.auth.domain.service.UserAuthenticationService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.refreshToken() {

    val tokenBuilder by  inject<TokenBuilder>()
    val userAuthenticationService by inject<UserAuthenticationService>()

    get("/{refreshId}") {

        val refreshId = call.parameters["refreshId"]!!

        val authentication = userAuthenticationService.authenticateUser(refreshId)
        val response = tokenBuilder(authentication)

        call.respond(hashMapOf("tokens" to response))
    }
}
