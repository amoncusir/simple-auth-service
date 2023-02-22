package info.digitalpoet.auth.domain.command.authentication

import info.digitalpoet.auth.domain.InvalidUser
import info.digitalpoet.auth.domain.command.password.ValidatePassword
import info.digitalpoet.auth.domain.command.tracer.EventPublisher
import info.digitalpoet.auth.domain.model.Authentication
import info.digitalpoet.auth.domain.model.AuthenticationScope
import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.domain.values.RefreshId
import java.time.Instant

interface AuthenticationIssuer
{
    class Request(
        val email: String,
        val rawPassword: CharArray,
        val scope: Map<String, List<String>>,
        val clientId: String,
        val ttl: Long,
        val withRefresh: Boolean,
    )

    operator fun invoke(request: Request): Authentication

    operator fun invoke(refreshId: RefreshId): Authentication
}

class UserPolicyValidatorAuthenticationIssuer(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val validatePassword: ValidatePassword,
    private val policyValidator: PolicyValidator,
    private val eventPublisher: EventPublisher,
    private val jwtTtl: Long
): AuthenticationIssuer
{
    override fun invoke(request: AuthenticationIssuer.Request): Authentication
    {
        val user = userRepository.findUserByEmail(Email(request.email))

        if (!user.isValid()) throw InvalidUser("Invalid userId: ${user.userId}")

        validatePassword(user, request.rawPassword)

        val scope = buildScope(request.scope)

        policyValidator(user, scope)

        val auth = Authentication(
            user,
            scope,
            request.clientId,
            Instant.now().plusSeconds(request.ttl),
            if (request.withRefresh) RefreshId.new() else null
        )

        if (request.withRefresh) authenticationRepository.save(auth)

        eventPublisher("login.success", mapOf("from" to request, "auth" to auth))

        return auth
    }

    override fun invoke(refreshId: RefreshId): Authentication
    {
        val authentication = authenticationRepository.delete(refreshId)

        if (!authentication.user.isValid()) throw InvalidUser("Invalid userId: ${authentication.user.userId}")

        policyValidator(authentication.user, authentication.scope)

        val newAuth = authentication.newAuth(refreshId = RefreshId.new(), Instant.now().plusSeconds(jwtTtl))

        authenticationRepository.save(newAuth)

        eventPublisher("refresh.success", mapOf("from" to refreshId, "auth" to newAuth))

        return newAuth
    }

    private fun buildScope(scope: Map<String, List<String>>): List<AuthenticationScope> {
        return scope
            .entries
            .map { AuthenticationScope(it.key, it.value) }
    }
}