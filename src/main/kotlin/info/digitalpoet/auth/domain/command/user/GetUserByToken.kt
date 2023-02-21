package info.digitalpoet.auth.domain.command.user

import info.digitalpoet.auth.domain.entity.Token
import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.repository.UserRepository

interface GetUserByToken {

    operator fun invoke(token: Token): User
}

class SimpleRepositoryGetUserByToken(
    private val userRepository: UserRepository
): GetUserByToken
{
    override fun invoke(token: Token): User {
        return userRepository.findById(token.userId)
    }
}