package info.digitalpoet.auth.domain.command.user

import info.digitalpoet.auth.domain.command.tracer.EventPublisher
import info.digitalpoet.auth.domain.model.Policy
import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.domain.values.UserId

interface UpdateUserPolicies
{
    operator fun invoke(id: UserId, policies: List<Policy>): User

    operator fun invoke(email: Email, policies: List<Policy>): User
}

class RepositoryUpdateUserPolicies(
    private val userRepository: UserRepository,
    private val eventPublisher: EventPublisher
): UpdateUserPolicies
{
    override fun invoke(id: UserId, policies: List<Policy>): User {
        return update(policies) {
            userRepository.findById(id)
        }
    }

    override fun invoke(email: Email, policies: List<Policy>): User {
        return update(policies) {
            userRepository.findUserByEmail(email)
        }
    }

    private fun update(policies: List<Policy>, user: () -> User): User
    {
        val updatedUser = user().copy(policies = policies)

        try { return userRepository.update(updatedUser) }
        finally { eventPublisher("user.edit.policy", mapOf("policies" to policies, "user" to updatedUser)) }
    }
}