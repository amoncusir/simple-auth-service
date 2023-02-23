package info.digitalpoet.auth.domain.command.authentication

import info.digitalpoet.auth.domain.command.tracer.EventPublisher
import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.values.UserId

interface InvalidateAuthentication
{
    operator fun invoke(id: UserId)
}

class RepositoryInvalidateAuthentication(
    private val authenticationRepository: AuthenticationRepository,
    private val eventPublisher: EventPublisher
): InvalidateAuthentication
{
    override fun invoke(id: UserId) {
        try { authenticationRepository.deleteByUserId(id) }
        finally { eventPublisher("authentication.invalidate", mapOf("userId" to id)) }
    }
}
