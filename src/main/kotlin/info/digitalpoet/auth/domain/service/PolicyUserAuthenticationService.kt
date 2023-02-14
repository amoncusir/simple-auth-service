package info.digitalpoet.auth.domain.service

import info.digitalpoet.auth.domain.cases.password.ValidatePasswordUseCase
import info.digitalpoet.auth.domain.model.Authentication
import info.digitalpoet.auth.domain.model.AuthenticationScope
import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.utils.ID
import info.digitalpoet.auth.utils.toHex
import java.security.MessageDigest
import java.time.Instant

class PolicyUserAuthenticationService(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val validatePassword: ValidatePasswordUseCase,
    private val userPolicyValidatorService: UserPolicyValidatorService,
    private val jwtTtl: Long
    ): UserAuthenticationService
{
    private val messageDigest = MessageDigest.getInstance("SHA-256")

    override fun authenticateUser(request: UserAuthenticationService.AuthenticationRequest): Authentication
    {
        val user = userRepository.findUserByEmail(request.email)

        if (!user.isValid()) throw InvalidUser("Invalid userId: ${user.userId}")

        validatePassword(user, request.rawPassword)

        val scope = buildScope(request.scope)

        userPolicyValidatorService.validate(user, scope)

        val auth = Authentication(
            user,
            scope,
            request.clientId,
            Instant.now().plusSeconds(request.ttl),
            if (request.withRefresh) generateRefreshToken() else null
        )

        if (request.withRefresh) authenticationRepository.save(auth)

        return auth
    }

    override fun authenticateUser(refreshId: String): Authentication
    {
        val authentication = authenticationRepository.delete(refreshId)

        if (!authentication.user.isValid()) throw InvalidUser("Invalid userId: ${authentication.user.userId}")

        userPolicyValidatorService.validate(authentication.user, authentication.scope)

        val newAuth = authentication.newAuth(refreshId = generateRefreshToken(), Instant.now().plusSeconds(jwtTtl))

        authenticationRepository.save(newAuth)

        return newAuth
    }

    private fun generateRefreshToken(): String {
        val randomId = ID.random()
        val digest = messageDigest.digest(randomId.encodeToByteArray())
        return digest.toHex()
    }

    private fun buildScope(scope: Map<String, List<String>>): List<AuthenticationScope> {
        return scope
            .entries
            .map { AuthenticationScope(it.key, it.value) }
    }
}
