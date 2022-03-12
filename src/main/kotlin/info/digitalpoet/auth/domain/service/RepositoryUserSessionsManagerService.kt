package info.digitalpoet.auth.domain.service

import info.digitalpoet.auth.domain.model.Authentication
import info.digitalpoet.auth.domain.repository.AuthenticationRepository

class RepositoryUserSessionsManagerService(
    private val authenticationRepository: AuthenticationRepository
): UserSessionsManagerService
{
    override fun invalidateRefreshTokens(userId: String)
    {
        authenticationRepository.deleteByUserId(userId)
    }

    override fun findActiveAuthentications(userId: String): List<Authentication>
    {
        return authenticationRepository.findByUserId(userId)
    }
}
