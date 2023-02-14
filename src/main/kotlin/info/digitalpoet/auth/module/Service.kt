package info.digitalpoet.auth.module

import de.mkammerer.argon2.Argon2Factory
import info.digitalpoet.auth.domain.cases.password.*
import info.digitalpoet.auth.domain.service.*
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.dsl.module

fun serviceModule(): Module
{
    return module(createdAtStart = true) {

        single { Argon2Wrapper(Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 128), iterations = 27) }
        single<EncodePasswordUseCase> { Argon2EncodePasswordService(get()) }
        single<ValidatePasswordUseCase> { Argon2ValidatePasswordService(get()) }

        single<TokenService> {

            val jwtConfig = get<Application>().environment.config.config("jwt")
            val configuration = JWTTokenService.Configuration(
                secret = jwtConfig.property("secret").getString(),
                issuer = jwtConfig.property("issuer").getString(),
                audience = jwtConfig.property("audience").getString(),
                ttl = jwtConfig.property("ttl").getString().toLong(),
            )

            JWTTokenService(configuration)
        }

        single<UserAuthenticationService> {
            val ttl = get<Application>().environment.config.property("jwt.ttl").getString().toLong()

            PolicyUserAuthenticationService(get(), get(), get(), get(), ttl)
        }

        single<UserSessionsManagerService> { RepositoryUserSessionsManagerService(get()) }

        single<UserService> { SimpleUserService(get(), get()) }

        single<UserPolicyValidatorService> { ValidAllUserPolicyValidatorService() }
    }
}
