package info.digitalpoet.auth.application.rest.user

import info.digitalpoet.auth.domain.model.Authentication
import info.digitalpoet.auth.domain.model.AuthenticationScope
import info.digitalpoet.auth.domain.model.Policy
import info.digitalpoet.auth.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val userId: String,
    val email: String,
    val policies: List<PolicyResponse>
) {
    constructor(from: User): this(
        from.userId.toString(),
        from.email.toString(),
        from.policies.policies.map { PolicyResponse(it) }
    )
}

fun User.toResponse() = UserResponse(this)

@Serializable
class UserRequest(
    val email: String,
    val password: CharArray
)

@Serializable
data class AuthenticationResponse(
    val client: String,
    val scope: List<PolicyResponse>
) {
    constructor(from: Authentication): this(
        from.client,
        from.scope.map { PolicyResponse(it) }
    )
}

fun Authentication.toResponse() = AuthenticationResponse(this)

@Serializable
data class PolicyResponse(
    val service: String,
    val actions: Set<String>
) {
    constructor(from: Policy): this(
        from.service,
        from.actions
    )

    constructor(from: AuthenticationScope): this(
        from.service,
        from.grant
    )
}
