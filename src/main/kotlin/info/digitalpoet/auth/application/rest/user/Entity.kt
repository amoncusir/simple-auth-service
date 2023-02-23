package info.digitalpoet.auth.application.rest.user

import info.digitalpoet.auth.domain.model.Authentication
import info.digitalpoet.auth.domain.model.AuthenticationScope
import info.digitalpoet.auth.domain.model.Policy
import info.digitalpoet.auth.domain.model.User

data class UserResponse(
    val userId: String,
    val email: String,
    val policies: List<Policy>
)

fun User.toResponse() = UserResponse(userId.toString(), email.toString(), policies.policies.toList())

class UserRequest(
    val email: String,
    val password: CharArray
)

data class AuthenticationResponse(
    val client: String,
    val scope: List<AuthenticationScope>
)

fun Authentication.toResponse() = AuthenticationResponse(client, scope)
