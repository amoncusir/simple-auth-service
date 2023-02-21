package info.digitalpoet.auth.module

import de.mkammerer.argon2.Argon2Factory
import info.digitalpoet.auth.domain.command.password.*
import info.digitalpoet.auth.domain.command.token.JWTTokenBuilder
import info.digitalpoet.auth.domain.command.token.TokenBuilder
import info.digitalpoet.auth.domain.service.*
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.dsl.module

fun serviceModule(): Module
{
    return module(createdAtStart = true) {

        single { Argon2Wrapper(Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 128), iterations = 27) }
        single<EncodePassword> { Argon2EncodePasswordService(get()) }
        single<ValidatePassword> { Argon2ValidatePasswordService(get(), get()) }

        single<TokenBuilder> {

            val jwtConfig = get<Application>().environment.config.config("jwt")
            val configuration = JWTTokenBuilder.Configuration(
                secret = jwtConfig.property("secret").getString(),
                issuer = jwtConfig.property("issuer").getString(),
                audience = jwtConfig.property("audience").getString(),
                ttl = jwtConfig.property("ttl").getString().toLong(),
            )

            JWTTokenBuilder(configuration)
        }

        single<UserAuthenticationService> {
            val ttl = get<Application>().environment.config.property("jwt.ttl").getString().toLong()

            PolicyUserAuthenticationService(get(), get(), get(), get(), get(), ttl)
        }

        single<UserSessionsManagerService> { RepositoryUserSessionsManagerService(get()) }

        single<UserService> { SimpleUserService(get(), get()) }

        single<UserPolicyValidatorService> { ValidAllUserPolicyValidatorService() }
    }
}
