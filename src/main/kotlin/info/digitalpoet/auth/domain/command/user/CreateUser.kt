package info.digitalpoet.auth.domain.command.user

import info.digitalpoet.auth.domain.command.password.EncodePassword
import info.digitalpoet.auth.domain.command.tracer.EventPublisher
import info.digitalpoet.auth.domain.model.Policies
import info.digitalpoet.auth.domain.model.Policy
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
    private val encodePassword: EncodePassword,
    private val eventPublisher: EventPublisher
): CreateUser
{
    companion object {
        private val DEFAULT_POLICY = Policies(Policy("auth", "self"))
    }

    override operator fun invoke(create: CreateUser.Request): User {
        val hashedPassword = encodePassword(create.plainPassword)
        val user = User(UserId.new(), Email(create.email), hashedPassword, true, DEFAULT_POLICY)

        try { return userRepository.save(user) }
        finally { eventPublisher("user.new", mapOf("user" to user)) }
    }
}