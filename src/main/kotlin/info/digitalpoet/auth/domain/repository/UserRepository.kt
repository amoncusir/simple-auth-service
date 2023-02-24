package info.digitalpoet.auth.domain.repository

import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.domain.values.UserId

interface UserRepository: CrudRepository<UserId, User>
{
    fun findUserByEmail(email: Email): User
}
