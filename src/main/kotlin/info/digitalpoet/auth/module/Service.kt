package info.digitalpoet.auth.module

import info.digitalpoet.auth.domain.service.JWTTokenService
import info.digitalpoet.auth.domain.service.PasswordManagerService
import info.digitalpoet.auth.domain.service.PolicyUserAuthenticationService
import info.digitalpoet.auth.domain.service.DigestPasswordManagerService
import info.digitalpoet.auth.domain.service.RepositoryUserSessionsManagerService
import info.digitalpoet.auth.domain.service.SimpleUserService
import info.digitalpoet.auth.domain.service.TokenService
import info.digitalpoet.auth.domain.service.UserAuthenticationService
import info.digitalpoet.auth.domain.service.UserSessionsManagerService
import info.digitalpoet.auth.domain.service.UserService
import info.digitalpoet.auth.domain.service.UserPolicyValidatorService
import info.digitalpoet.auth.domain.service.ValidAllUserPolicyValidatorService
import io.ktor.application.Application
import org.koin.core.module.Module
import org.koin.dsl.module
import java.security.MessageDigest

fun serviceModule(): Module
{
    return module(createdAtStart = true) {

        single<PasswordManagerService> { DigestPasswordManagerService(MessageDigest.getInstance("SHA-512")) }

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
