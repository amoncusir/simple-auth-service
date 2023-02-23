package info.digitalpoet.auth.application.rest.user

import info.digitalpoet.auth.domain.command.authentication.FindActiveAuthentications
import info.digitalpoet.auth.domain.command.authentication.InvalidateAuthentication
import info.digitalpoet.auth.domain.entity.Token
import info.digitalpoet.auth.domain.values.UserId
import info.digitalpoet.auth.plugins.security.authenticateAdmin
import info.digitalpoet.auth.plugins.security.authenticateSelf
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userSessions() {

    val invalidateAuthentication by inject<InvalidateAuthentication>()
    val findActiveAuthentications by inject<FindActiveAuthentications>()

    route("user/sessions") {
        authenticateSelf {
            delete("/invalidate") {
                val token = call.principal<Token>()!!

                invalidateAuthentication(token.userId)
            }

            get {
                val token = call.principal<Token>()!!

                val authentications = findActiveAuthentications(token.userId)
                    .map { it.toResponse() }

                call.respond(hashMapOf("authentications" to authentications))
            }
        }

        authenticateAdmin {
            route("/{userId}") {
                delete("/invalidate") {
                    val userId = call.parameters["userId"]!!

                    invalidateAuthentication(UserId(userId))
                }

                get {
                    val userId = call.parameters["userId"]!!

                    val authentications = findActiveAuthentications(UserId(userId))
                        .map { it.toResponse() }

                    call.respond(hashMapOf("authentications" to authentications))
                }
            }
        }
    }
}
