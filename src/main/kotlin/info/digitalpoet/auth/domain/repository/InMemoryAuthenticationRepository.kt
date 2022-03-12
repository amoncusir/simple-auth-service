package info.digitalpoet.auth.domain.repository

import info.digitalpoet.auth.domain.model.Authentication

class InMemoryAuthenticationRepository: AuthenticationRepository
{
    private val cache = HashMap<String, Authentication>()

    override fun save(authentication: Authentication): Authentication
    {
        cache[authentication.refreshId!!] = authentication.copy()

        return authentication
    }

    override fun delete(refreshId: String): Authentication
    {
        return cache.remove(refreshId) ?: throw NotFoundEntity(refreshId, "Authentication")
    }
}
