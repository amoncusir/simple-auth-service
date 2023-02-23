package info.digitalpoet.auth.domain.command.authentication

import info.digitalpoet.auth.domain.InvalidPolicies
import info.digitalpoet.auth.domain.InvalidRefreshId
import info.digitalpoet.auth.domain.InvalidUser
import info.digitalpoet.auth.domain.command.password.ValidatePassword
import info.digitalpoet.auth.domain.command.tracer.EventPublisher
import info.digitalpoet.auth.domain.model.Authentication
import info.digitalpoet.auth.domain.model.AuthenticationScope
import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.repository.NotFoundEntity
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
    private val refreshTokenTtl: Long
): AuthenticationIssuer
{
    override fun invoke(request: AuthenticationIssuer.Request): Authentication
    {
        val user = try {
            userRepository.findUserByEmail(Email(request.email))
        } catch (e: NotFoundEntity) {
            throw InvalidUser("User Not found by email: ${request.email}")
        }

        if (!user.isValid()) {
            eventPublisher("login.invalid.user", mapOf("user" to user))
            throw InvalidUser("Invalid userId: ${user.userId}")
        }

        validatePassword(user, request.rawPassword)

        val scope = buildScope(request.scope)

        if(!policyValidator(user, scope)) {
            eventPublisher("login.invalid.policies", mapOf("user" to user, "scope" to scope))
            throw InvalidPolicies("Invalid requested policies for user: ${user.userId}")
        }

        val auth = Authentication(
            user,
            scope,
            request.clientId,
            Instant.now().plusSeconds(refreshTokenTtl),
            if (request.withRefresh) RefreshId.new() else null
        )

        if (request.withRefresh) authenticationRepository.save(auth)

        eventPublisher("login.success", mapOf("from" to request, "auth" to auth))

        return auth
    }

    override fun invoke(refreshId: RefreshId): Authentication
    {
        val authentication = try {
            authenticationRepository.delete(refreshId)
        } catch (e: NotFoundEntity) {
            throw InvalidRefreshId("Not Found refreshId: $refreshId")
        }

        if(authentication.ttl.isBefore(Instant.now())) {
            throw InvalidRefreshId("Expired refreshId: $refreshId")
        }

        if (!authentication.user.isValid()) throw InvalidUser("Invalid userId: ${authentication.user.userId}")

        policyValidator(authentication.user, authentication.scope)

        val newAuth = authentication.newAuth(refreshId = RefreshId.new(), Instant.now().plusSeconds(refreshTokenTtl))

        authenticationRepository.save(newAuth)

        eventPublisher("refresh.success", mapOf("from" to refreshId, "auth" to newAuth))

        return newAuth
    }

    private fun buildScope(scope: Map<String, List<String>>): List<AuthenticationScope> {
        return scope
            .entries
            .map { AuthenticationScope(it.key, it.value.toSet()) }
    }
}