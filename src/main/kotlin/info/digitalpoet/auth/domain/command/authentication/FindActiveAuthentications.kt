package info.digitalpoet.auth.domain.command.authentication

import info.digitalpoet.auth.domain.model.Authentication
import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.values.UserId

interface FindActiveAuthentications
{
    operator fun invoke(id: UserId): List<Authentication>
}

class AuthRepositoryFindActiveAuthentications(
    private val authenticationRepository: AuthenticationRepository
): FindActiveAuthentications
{
    override fun invoke(id: UserId): List<Authentication> {
        return authenticationRepository.findByUserId(id)
    }
}
