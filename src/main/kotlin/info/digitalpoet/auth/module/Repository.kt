package info.digitalpoet.auth.module

import info.digitalpoet.auth.domain.model.Policy
import info.digitalpoet.auth.domain.model.PolicyEffect
import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.repository.InMemoryAuthenticationRepository
import info.digitalpoet.auth.domain.repository.InMemoryUserRepository
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.service.PasswordManagerService
import org.koin.core.module.Module
import org.koin.dsl.module

fun repositoryModule(): Module
{
    return module(createdAtStart = true) {
        single<UserRepository> {
            val passwordManagerService = get<PasswordManagerService>()

            val password = passwordManagerService.encode("mondieu")

            InMemoryUserRepository(listOf(
                User("my-id", "aran@digitalpoet.info", password, true, listOf(Policy("*", listOf("*"), PolicyEffect.ALLOW)))
            ))
        }
        single<AuthenticationRepository> { InMemoryAuthenticationRepository() }
    }
}
