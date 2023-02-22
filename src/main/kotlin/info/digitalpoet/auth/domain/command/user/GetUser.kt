package info.digitalpoet.auth.domain.command.user

import info.digitalpoet.auth.domain.entity.Token
import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.domain.values.UserId

interface GetUser {

    operator fun invoke(id: UserId): User

    operator fun invoke(email: Email): User

    operator fun invoke(token: Token): User = invoke(token.userId)
}

class SimpleRepositoryGetUser(
    private val userRepository: UserRepository
): GetUser
{
    override fun invoke(id: UserId): User {
        return userRepository.findById(id)
    }

    override fun invoke(email: Email): User {
        return userRepository.findUserByEmail(email)
    }
}