package info.digitalpoet.auth.application.rest.user

import info.digitalpoet.auth.application.rest.UnauthorizedPetition
import info.digitalpoet.auth.domain.entity.Token
import info.digitalpoet.auth.domain.model.Policy
import info.digitalpoet.auth.domain.service.UserService
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import org.koin.ktor.ext.inject

data class UserResponse(
    val userId: String,
    val email: String,
    val policies: List<Policy>
)

fun Route.getUser() {

    val userFinder by inject<UserService>()

    get {
        val token = call.authentication.principal<Token>() ?: throw UnauthorizedPetition()
        val user = userFinder.getUserById(token.userId)
        val response = UserResponse(user.userId, user.email, user.policies)

        call.respond(hashMapOf("user" to response))
    }

}
