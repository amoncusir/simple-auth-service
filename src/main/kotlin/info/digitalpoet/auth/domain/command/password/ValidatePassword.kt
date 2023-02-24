package info.digitalpoet.auth.domain.command.password

import info.digitalpoet.auth.domain.InvalidPassword
import info.digitalpoet.auth.domain.command.tracer.EventPublisher
import info.digitalpoet.auth.domain.model.User

interface ValidatePassword {

    operator fun invoke(user: User, plainPassword: CharArray)
}

class Argon2ValidatePasswordService(private val eventPublisher: EventPublisher,
                                    private val argon: Argon2Wrapper): ValidatePassword
{
    override fun invoke(user: User, plainPassword: CharArray)
    {
        if (!argon.validate(user.hashedPassword, plainPassword))
        {
            eventPublisher("password.fail", mapOf("user" to user))
            throw InvalidPassword("Invalid password for user ${user.userId}")
        }
    }
}