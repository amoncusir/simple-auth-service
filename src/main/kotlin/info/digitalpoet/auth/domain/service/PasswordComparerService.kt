package info.digitalpoet.auth.domain.service

import info.digitalpoet.auth.domain.model.User

interface PasswordComparerService
{
    fun validate(user: User, plainPassword: String)

    fun encode(plainPassword: String): String
}
