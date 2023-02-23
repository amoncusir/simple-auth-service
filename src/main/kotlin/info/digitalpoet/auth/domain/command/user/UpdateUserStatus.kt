package info.digitalpoet.auth.domain.command.user

import info.digitalpoet.auth.domain.command.tracer.EventPublisher
import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.domain.values.UserId

interface UpdateUserStatus
{
    operator fun invoke(id: UserId, isActive: Boolean): User
    operator fun invoke(email: Email, isActive: Boolean): User
}

class RepositoryUpdateUserStatus(
    private val userRepository: UserRepository,
    private val eventPublisher: EventPublisher
): UpdateUserStatus
{
    override fun invoke(id: UserId, isActive: Boolean): User {
        return update(isActive) {
            userRepository.findById(id)
        }
    }

    override fun invoke(email: Email, isActive: Boolean): User {
        return update(isActive) {
            userRepository.findUserByEmail(email)
        }
    }

    private fun update(isActive: Boolean, user: () -> User): User
    {
        val updatedUser = user().copy(isActive = isActive)

        try { return userRepository.update(updatedUser) }
        finally { eventPublisher("user.edit.isActive", mapOf("isActive" to isActive, "user" to updatedUser)) }
    }
}
