package info.digitalpoet.auth.module

import info.digitalpoet.auth.domain.cases.password.B64EncodePasswordService
import info.digitalpoet.auth.domain.cases.password.EncodePasswordUseCase
import info.digitalpoet.auth.domain.cases.password.ValidatePasswordService
import info.digitalpoet.auth.domain.cases.password.ValidatePasswordUseCase
import info.digitalpoet.auth.domain.service.*
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.dsl.module
import java.security.MessageDigest

fun serviceModule(): Module
{
    return module(createdAtStart = true) {

        single<EncodePasswordUseCase> { B64EncodePasswordService(MessageDigest.getInstance("SHA-512")) }
        single<ValidatePasswordUseCase> { ValidatePasswordService(get()) }

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
