package info.digitalpoet.auth.application.rest.user

import info.digitalpoet.auth.domain.command.user.GetUser
import info.digitalpoet.auth.domain.entity.Token
import info.digitalpoet.auth.domain.values.UserId
import info.digitalpoet.auth.plugins.security.authenticateAdmin
import info.digitalpoet.auth.plugins.security.authenticateSelf
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.getUser() {

    val userFinder by inject<GetUser>()

    authenticateSelf {
        get("/user") {
            val token = call.principal<Token>()!!
            val user = userFinder(token)

            call.respond(hashMapOf("user" to user.toResponse()))
        }
    }

    authenticateAdmin {
        get("/user/id/{userId}") {
            val userId = call.parameters["userId"]!!
            val user = userFinder(UserId(userId))

            call.respond(hashMapOf("user" to user.toResponse()))
        }
    }
}
