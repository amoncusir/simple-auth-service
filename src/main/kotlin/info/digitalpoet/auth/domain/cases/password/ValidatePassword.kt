package info.digitalpoet.auth.domain.cases.password

import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.service.InvalidPassword

interface ValidatePasswordUseCase {

    operator fun invoke(user: User, plainPassword: CharArray)
}

class ValidatePasswordService(private val encodePassword: EncodePasswordUseCase): ValidatePasswordUseCase {
    override fun invoke(user: User, plainPassword: CharArray) {
        if (user.hashedPassword != encodePassword(plainPassword))
            throw InvalidPassword("Invalid password for user ${user.userId}")
    }
}

class Argon2ValidatePasswordService(private val argon: Argon2Wrapper): ValidatePasswordUseCase
{
    override fun invoke(user: User, plainPassword: CharArray)
    {
        if (!argon.validate(user.hashedPassword, plainPassword))
            throw InvalidPassword("Invalid password for user ${user.userId}")
    }
}