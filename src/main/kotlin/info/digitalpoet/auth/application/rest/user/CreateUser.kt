package info.digitalpoet.auth.application.rest.user

import io.ktor.routing.Route
import io.ktor.routing.post

data class UserRequest(
    val email: String,
    val password: String
)
