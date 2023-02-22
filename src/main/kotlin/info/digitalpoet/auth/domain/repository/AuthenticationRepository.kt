package info.digitalpoet.auth.domain.repository

import info.digitalpoet.auth.domain.model.Authentication
import info.digitalpoet.auth.domain.values.RefreshId
import info.digitalpoet.auth.domain.values.UserId

interface AuthenticationRepository
{
    fun save(authentication: Authentication): Authentication

    fun delete(refreshId: RefreshId): Authentication

    fun deleteByUserId(userId: UserId): List<Authentication>

    fun findByUserId(userId: UserId): List<Authentication>
}
