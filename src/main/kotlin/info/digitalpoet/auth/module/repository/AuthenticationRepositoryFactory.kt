package info.digitalpoet.auth.module.repository

import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.repository.memory.InMemoryAuthenticationRepository
import info.digitalpoet.auth.module.InvalidProvider
import org.koin.core.scope.Scope

fun Scope.authenticationRepositoryFactory(provider: String): AuthenticationRepository
{
    return when(provider) {
        "inmemory" -> authenticationRepositoryInMemory()
        else -> throw InvalidProvider(AuthenticationRepository::class, provider)
    }
}

fun Scope.authenticationRepositoryInMemory(): AuthenticationRepository = InMemoryAuthenticationRepository()
