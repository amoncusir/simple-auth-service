package info.digitalpoet.auth.module.repository

import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.repository.memory.InMemoryUserRepository
import info.digitalpoet.auth.module.InvalidProvider
import org.koin.core.scope.Scope

fun Scope.userRepositoryFactory(provider: String): UserRepository
{
    return when(provider) {
        "inmemory" -> userRepositoryInMemory()
        else -> throw InvalidProvider(UserRepository::class, provider)
    }
}

fun Scope.userRepositoryInMemory(): UserRepository = InMemoryUserRepository()
