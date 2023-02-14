package info.digitalpoet.auth.application.rest.authentication

import info.digitalpoet.auth.domain.service.TokenService
import info.digitalpoet.auth.domain.service.UserAuthenticationService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.refreshToken() {

    val tokenService by  inject<TokenService>()
    val userAuthenticationService by inject<UserAuthenticationService>()

    get("/{refreshId}") {

        val refreshId = call.parameters["refreshId"]!!

        val authentication = userAuthenticationService.authenticateUser(refreshId)
        val response = tokenService.buildToken(authentication)

        call.respond(hashMapOf("tokens" to response))
    }
}
