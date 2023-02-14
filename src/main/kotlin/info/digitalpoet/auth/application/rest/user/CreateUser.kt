package info.digitalpoet.auth.application.rest.user

import info.digitalpoet.auth.domain.service.UserService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.createUser() {

    val userService by inject<UserService>()

    post {
        val userRequest: UserRequest = call.receive()

        val user = userService.createUser(UserService.CreateUser(userRequest.email, userRequest.password))

        call.respond(hashMapOf("user" to user.toResponse()))
    }
}
