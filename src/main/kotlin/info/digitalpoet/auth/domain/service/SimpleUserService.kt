package info.digitalpoet.auth.domain.service

import info.digitalpoet.auth.domain.model.Policy
import info.digitalpoet.auth.domain.model.PolicyEffect
import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.utils.ID

class SimpleUserService(
    private val userRepository: UserRepository,
    private val passwordManagerService: PasswordManagerService
): UserService
{
    companion object {
        private val DEFAULT_POLICY = listOf(Policy("auth", listOf("*"), PolicyEffect.ALLOW))
    }

    override fun getUserById(userId: String): User
    {
        return userRepository.findById(userId)
    }

    override fun createUser(create: UserService.CreateUser): User
    {
        val hashedPassword = passwordManagerService.encode(create.plainPassword)
        val user = User(ID.random(), create.email, hashedPassword, true, DEFAULT_POLICY)

        return userRepository.save(user)
    }
}
