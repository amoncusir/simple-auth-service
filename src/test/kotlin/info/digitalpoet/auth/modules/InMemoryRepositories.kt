package info.digitalpoet.auth.modules

import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.repository.InMemoryAuthenticationRepository
import info.digitalpoet.auth.domain.repository.InMemoryUserRepository
import info.digitalpoet.auth.domain.repository.UserRepository
import org.koin.dsl.bind
import org.koin.dsl.module

fun testInMemoryRepositories() = module {

    single { InMemoryUserRepository() } bind UserRepository::class

    single { InMemoryAuthenticationRepository() } bind AuthenticationRepository::class
}
