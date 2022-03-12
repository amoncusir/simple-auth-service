package info.digitalpoet.auth.domain.repository

import info.digitalpoet.auth.domain.model.Authentication

interface AuthenticationRepository
{
    fun save(authentication: Authentication): Authentication

    fun delete(refreshId: String): Authentication
}
