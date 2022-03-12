package info.digitalpoet.auth.application.rest.user

import info.digitalpoet.auth.application.rest.UnauthorizedPetition
import info.digitalpoet.auth.domain.entity.Token
import info.digitalpoet.auth.domain.service.UserSessionsManagerService
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import org.koin.ktor.ext.inject

fun Route.userSessions() {

    val userSessionsManagerService by inject<UserSessionsManagerService>()

    delete("/invalidate") {
        val token = call.authentication.principal<Token>() ?: throw UnauthorizedPetition()

        userSessionsManagerService.invalidateRefreshTokens(token.userId)
    }

    get {
        val token = call.authentication.principal<Token>() ?: throw UnauthorizedPetition()

        val authentications = userSessionsManagerService.findActiveAuthentications(token.userId)
            .map { it.toResponse() }

        call.respond(hashMapOf("authentications" to authentications))
    }
}
