package info.digitalpoet.auth.application.rest.user

import info.digitalpoet.auth.domain.service.UserService
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.ktor.ext.inject

fun Route.createUser() {

    val userService by inject<UserService>()

    post {
        val userRequest: UserRequest = call.receive()

        val user = userService.createUser(UserService.CreateUser(userRequest.email, userRequest.password))

        call.respond(hashMapOf("user" to user.toResponse()))
    }
}
