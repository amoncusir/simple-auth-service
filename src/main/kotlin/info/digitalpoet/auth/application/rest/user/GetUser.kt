package info.digitalpoet.auth.application.rest.user

import info.digitalpoet.auth.domain.command.user.GetUserByToken
import info.digitalpoet.auth.domain.entity.Token
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.getUser() {

    val userFinder by inject<GetUserByToken>()

    get("/user") {
        val token = call.principal<Token>()!!
        val user = userFinder(token)

        call.respond(hashMapOf("user" to user.toResponse()))
    }
}
