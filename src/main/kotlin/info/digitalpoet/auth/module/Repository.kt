package info.digitalpoet.auth.module

import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.repository.Repository
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.repository.memory.InMemoryAuthenticationRepository
import info.digitalpoet.auth.domain.repository.memory.InMemoryUserRepository
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

inline fun <reified T: Repository> Scope.getRepository(): T = get(named(get(named("repository-impl"))))

fun repositoryModule(): Module
{
    return module(createdAtStart = true) {

        single(named("repository-impl")) {
            get<Application>().environment.config.property("factory.repository").getString()
        }

        single<UserRepository>(named("inmemory")) { InMemoryUserRepository() }
        single<AuthenticationRepository>(named("inmemory")) { InMemoryAuthenticationRepository() }
    }
}
