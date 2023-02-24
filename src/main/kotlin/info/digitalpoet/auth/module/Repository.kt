package info.digitalpoet.auth.module

import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.repository.Repository
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.repository.memory.InMemoryAuthenticationRepository
import info.digitalpoet.auth.domain.repository.memory.InMemoryUserRepository
import info.digitalpoet.auth.infrastructure.mongodb.MongoDBAuthenticationRepository
import info.digitalpoet.auth.infrastructure.mongodb.MongoDBUserRepository
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.koin.ktor.ext.get

inline fun <reified T: Repository> Scope.getRepository(): T = get(named(get(named("repository-impl"))))
inline fun <reified T: Repository> Application.getRepository(): T = get(named(get(named("repository-impl"))))

fun repositoryModule(): Module
{
    return module(createdAtStart = true) {

        single(named("repository-impl")) {
            get<Application>().environment.config.property("factory.repository").getString()
        }

        single<UserRepository>(named("inmemory")) { InMemoryUserRepository() }
        single<AuthenticationRepository>(named("inmemory")) { InMemoryAuthenticationRepository() }

        single<UserRepository>(named("mongodb")) {
            MongoDBUserRepository(get())
        }
        single<AuthenticationRepository>(named("mongodb")) {
            MongoDBAuthenticationRepository(get())
        }
    }
}
