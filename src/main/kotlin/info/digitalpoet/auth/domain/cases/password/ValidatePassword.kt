package info.digitalpoet.auth.domain.cases.password

import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.service.InvalidPassword

interface ValidatePasswordUseCase {

    operator fun invoke(user: User, plainPassword: String)
}

class ValidatePasswordService(private val encodePassword: EncodePasswordUseCase): ValidatePasswordUseCase {
    override fun invoke(user: User, plainPassword: String) {
        if (user.hashedPassword != encodePassword(plainPassword))
            throw InvalidPassword("Invalid password for user ${user.userId}")
    }
}