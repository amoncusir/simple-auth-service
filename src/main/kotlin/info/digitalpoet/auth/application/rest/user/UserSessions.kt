package info.digitalpoet.auth.application.rest.user

import info.digitalpoet.auth.application.rest.UnauthorizedPetition
import info.digitalpoet.auth.domain.entity.Token
import info.digitalpoet.auth.domain.service.UserSessionsManagerService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userSessions() {

    val userSessionsManagerService by inject<UserSessionsManagerService>()

    delete("/invalidate") {
        val token = call.principal<Token>() ?: throw UnauthorizedPetition()

        userSessionsManagerService.invalidateRefreshTokens(token.userId)
    }

    get {
        val token = call.principal<Token>() ?: throw UnauthorizedPetition()

        val authentications = userSessionsManagerService.findActiveAuthentications(token.userId)
            .map { it.toResponse() }

        call.respond(hashMapOf("authentications" to authentications))
    }
}
