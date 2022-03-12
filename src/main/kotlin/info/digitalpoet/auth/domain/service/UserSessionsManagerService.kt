package info.digitalpoet.auth.domain.service

import info.digitalpoet.auth.domain.model.Authentication

interface UserSessionsManagerService
{
    fun invalidateRefreshTokens(userId: String)

    fun findActiveAuthentications(userId: String): List<Authentication>
}
