package info.digitalpoet.auth.module

import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.module.repository.authenticationRepositoryFactory
import info.digitalpoet.auth.module.repository.userRepositoryFactory
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.dsl.module

fun repositoryModule(): Module
{
    return module(createdAtStart = true) {
        single<UserRepository> {
            val factoryConf = get<Application>().environment.config.config("factory")
            this.userRepositoryFactory(factoryConf.property("user-repository").getString())
        }

        single<AuthenticationRepository> {
            val factoryConf = get<Application>().environment.config.config("factory")
            this.authenticationRepositoryFactory(factoryConf.property("authentication-repository").getString())
        }
    }
}
