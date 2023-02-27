package info.digitalpoet.auth.domain.repository.memory

import info.digitalpoet.auth.domain.model.Authentication
import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.repository.InvalidAuthentication
import info.digitalpoet.auth.domain.repository.NotFoundEntity
import info.digitalpoet.auth.domain.values.RefreshId
import info.digitalpoet.auth.domain.values.UserId
import java.time.Instant
import java.time.LocalDateTime

class InMemoryAuthenticationRepository: AuthenticationRepository
{
    private val cache = HashMap<String, Authentication>()

    override fun save(authentication: Authentication): Authentication
    {
        if (authentication.refreshId == null)
            throw InvalidAuthentication("Only can save authentication with refreshId!")

        cache[authentication.refreshId.toString()] = authentication.copy()

        return authentication
    }

    override fun delete(refreshId: RefreshId): Authentication
    {
        return cache.remove(refreshId.toString()) ?: throw NotFoundEntity(refreshId.toString(), "Authentication")
    }

    override fun deleteByUserId(userId: UserId): List<Authentication>
    {
        val toRemoveAuth = findByUserId(userId)

        toRemoveAuth.forEach { cache.remove(it.refreshId.toString()) }

        return toRemoveAuth
    }

    override fun findByUserId(userId: UserId): List<Authentication>
    {
        val now = LocalDateTime.now()
        val auths = cache
            .entries
            .map { it.value }
            .filter { it.user.userId == userId }

        auths.filter { it.ttl.isAfter(now) }
            .forEach { delete(it.refreshId!!) }

        return auths.filter { !it.ttl.isAfter(now) }
    }

    fun clear() {
        cache.clear()
    }
}
