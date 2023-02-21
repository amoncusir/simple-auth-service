package info.digitalpoet.auth.domain.command.password

import info.digitalpoet.auth.domain.command.tracer.EventPublisher
import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.service.InvalidPassword

interface ValidatePassword {

    operator fun invoke(user: User, plainPassword: CharArray)
}

class EncodingEqualityValidatePasswordService(private val eventPublisher: EventPublisher,
                                              private val encodePassword: EncodePassword): ValidatePassword {
    override fun invoke(user: User, plainPassword: CharArray) {
        if (user.hashedPassword != encodePassword(plainPassword))
        {
            eventPublisher("validate.password.fail", mapOf("user" to user))
            throw InvalidPassword("Invalid password for user ${user.userId}")
        }
    }
}

class Argon2ValidatePasswordService(private val eventPublisher: EventPublisher,
                                    private val argon: Argon2Wrapper): ValidatePassword
{
    override fun invoke(user: User, plainPassword: CharArray)
    {
        if (!argon.validate(user.hashedPassword, plainPassword))
        {
            eventPublisher("validate.password.fail", mapOf("user" to user))
            throw InvalidPassword("Invalid password for user ${user.userId}")
        }
    }
}