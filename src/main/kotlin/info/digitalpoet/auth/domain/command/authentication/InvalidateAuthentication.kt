package info.digitalpoet.auth.domain.command.authentication

import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.values.UserId

interface InvalidateAuthentication
{
    operator fun invoke(id: UserId)
}

class RepositoryInvalidateAuthentication(
    private val authenticationRepository: AuthenticationRepository
): InvalidateAuthentication
{
    override fun invoke(id: UserId) {
        authenticationRepository.deleteByUserId(id)
    }
}
