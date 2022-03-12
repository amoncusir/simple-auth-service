package info.digitalpoet.auth.domain.repository

import info.digitalpoet.auth.domain.model.User

interface UserRepository: Repository<String, User>
{
    fun findUserByEmail(email: String): User
}
