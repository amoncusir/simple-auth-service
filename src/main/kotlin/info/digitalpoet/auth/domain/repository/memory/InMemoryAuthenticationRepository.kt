package info.digitalpoet.auth.domain.repository.memory

import info.digitalpoet.auth.domain.model.Authentication
import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.repository.NotFoundEntity

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

    override fun deleteByUserId(userId: String): List<Authentication>
    {
        val toRemoveAuth = findByUserId(userId)

        toRemoveAuth.forEach { cache.remove(it.refreshId) }

        return toRemoveAuth
    }

    override fun findByUserId(userId: String): List<Authentication>
    {
        return cache
            .entries
            .map { it.value }
            .filter { it.user.userId == userId }
    }

    fun clear() {
        cache.clear()
    }
}
