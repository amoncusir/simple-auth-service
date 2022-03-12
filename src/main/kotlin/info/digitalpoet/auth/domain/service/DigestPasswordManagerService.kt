package info.digitalpoet.auth.domain.service

import info.digitalpoet.auth.domain.model.User
import java.security.MessageDigest
import java.util.Base64

class DigestPasswordManagerService(private val messageDigest: MessageDigest) : PasswordManagerService
{
    private val encoder = Base64.getEncoder()

    override fun validate(user: User, plainPassword: String)
    {
        if (user.hashedPassword != encode(plainPassword))
            throw InvalidPassword()
    }

    override fun encode(plainPassword: String): String
    {
        return encoder.encodeToString(messageDigest.digest(plainPassword.encodeToByteArray()))
    }
}
