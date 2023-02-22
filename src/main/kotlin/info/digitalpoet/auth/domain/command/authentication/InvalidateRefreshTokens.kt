package info.digitalpoet.auth.domain.command.authentication

import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.values.UserId

interface InvalidateRefreshTokens
{
    operator fun invoke(id: UserId)
}

class AuthRepositoryInvalidateRefreshTokens(
    private val authenticationRepository: AuthenticationRepository
): InvalidateRefreshTokens
{
    override fun invoke(id: UserId) {
        authenticationRepository.deleteByUserId(id)
    }
}
