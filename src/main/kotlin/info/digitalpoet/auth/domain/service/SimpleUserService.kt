package info.digitalpoet.auth.domain.service

import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.repository.UserRepository

class SimpleUserService(
    private val userRepository: UserRepository
): UserService
{
    override fun getUserById(userId: String): User
    {
        return userRepository.findById(userId)
    }
}
