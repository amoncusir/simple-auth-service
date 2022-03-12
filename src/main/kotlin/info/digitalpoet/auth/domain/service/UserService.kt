package info.digitalpoet.auth.domain.service

import info.digitalpoet.auth.domain.model.User

interface UserService
{
    fun getUserById(userId: String): User
}
