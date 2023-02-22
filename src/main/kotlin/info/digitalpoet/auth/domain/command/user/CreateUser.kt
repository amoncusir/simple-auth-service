package info.digitalpoet.auth.domain.command.user

import info.digitalpoet.auth.domain.command.password.EncodePassword
import info.digitalpoet.auth.domain.model.Policy
import info.digitalpoet.auth.domain.model.PolicyEffect
import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.domain.values.UserId

interface CreateUser {

    class Request(
        val email: String,
        val plainPassword: CharArray
    )

    operator fun invoke(create: Request): User
}

class CreateUserSelfPolicy(
    private val userRepository: UserRepository,
    private val encodePassword: EncodePassword
): CreateUser
{
    companion object {
        private val DEFAULT_POLICY = listOf(Policy("auth", listOf("self"), PolicyEffect.ALLOW))
    }

    override operator fun invoke(create: CreateUser.Request): User {
        val hashedPassword = encodePassword(create.plainPassword)
        val user = User(UserId.new(), Email(create.email), hashedPassword, true, DEFAULT_POLICY)

        return userRepository.save(user)
    }
}