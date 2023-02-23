package info.digitalpoet.auth.module

import de.mkammerer.argon2.Argon2Factory
import info.digitalpoet.auth.domain.command.authentication.*
import info.digitalpoet.auth.domain.command.password.*
import info.digitalpoet.auth.domain.command.token.JWTTokenBuilder
import info.digitalpoet.auth.domain.command.token.TokenBuilder
import info.digitalpoet.auth.domain.command.user.*
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.dsl.module

fun serviceModule(): Module
{
    return module(createdAtStart = true) {

        single {
            val pwdConfig = get<Application>().environment.config.config("password-encoder")

            val type = Argon2Factory.Argon2Types.valueOf(pwdConfig.property("type").getString())
            val saltLength = pwdConfig.property("salt-length").getString().toInt()
            val hashLength = pwdConfig.property("hash-length").getString().toInt()
            val iterations = pwdConfig.property("iterations").getString().toInt()

            Argon2Wrapper(Argon2Factory.create(type, saltLength, hashLength), iterations = iterations)
        }
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

        single<AuthenticationIssuer> {
            val jwtTtl = get<Application>().environment.config.property("jwt.ttl").getString().toLong()
            val refreshTtl = get<Application>().environment.config.property("jwt.refresh-ttl-plus").getString().toLong()
            UserPolicyValidatorAuthenticationIssuer(get(), get(), get(), get(), get(), jwtTtl + refreshTtl)
        }

        single<InvalidateAuthentication> { RepositoryInvalidateAuthentication(get(), get()) }
        single<FindActiveAuthentications> { AuthRepositoryFindActiveAuthentications(get()) }

        single<CreateUser> { CreateUserSelfPolicy(get(), get(), get()) }
        single<GetUser> { SimpleRepositoryGetUser(get()) }
        single<UpdateUserPolicies> { RepositoryUpdateUserPolicies(get(), get()) }
        single<UpdateUserStatus> { RepositoryUpdateUserStatus(get(), get()) }

        single<PolicyValidator> { WildcardPolicyValidator() }
    }
}
