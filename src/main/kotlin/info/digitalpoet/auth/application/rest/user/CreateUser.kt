package info.digitalpoet.auth.application.rest.user

import info.digitalpoet.auth.domain.command.user.CreateUser
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.createUser() {

    val createUser by inject<CreateUser>()

    post {
        val userRequest: UserRequest = call.receive()

        val user = createUser(CreateUser.Request(userRequest.email, userRequest.password))

        call.respond(hashMapOf("user" to user.toResponse()))
    }
}
