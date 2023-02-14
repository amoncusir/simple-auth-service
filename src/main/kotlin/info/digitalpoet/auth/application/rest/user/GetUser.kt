package info.digitalpoet.auth.application.rest.user

import info.digitalpoet.auth.application.rest.UnauthorizedPetition
import info.digitalpoet.auth.domain.entity.Token
import info.digitalpoet.auth.domain.service.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.getUser() {

    val userFinder by inject<UserService>()

    get {
        val token = call.principal<Token>() ?: throw UnauthorizedPetition()
        val user = userFinder.getUserById(token.userId)

        call.respond(hashMapOf("user" to user.toResponse()))
    }
}
