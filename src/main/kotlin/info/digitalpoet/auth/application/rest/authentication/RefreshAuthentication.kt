package info.digitalpoet.auth.application.rest.authentication

import info.digitalpoet.auth.domain.service.TokenService
import info.digitalpoet.auth.domain.service.UserAuthenticationService
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import org.koin.ktor.ext.inject

fun Route.refreshToken() {

    val tokenService by  inject<TokenService>()
    val userAuthenticationService by inject<UserAuthenticationService>()

    get("/refresh/{refreshId}") {

        val refreshId = call.parameters["refreshId"]!!

        val authentication = userAuthenticationService.authenticateUser(refreshId)
        val response = tokenService.buildToken(authentication)

        call.respond(hashMapOf("tokens" to response))
    }
}
