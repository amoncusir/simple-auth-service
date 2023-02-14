package info.digitalpoet.auth.domain.cases.password

import java.security.MessageDigest
import java.util.*

interface EncodePasswordUseCase
{
    operator fun invoke(plainPassword: String): String
}

class B64EncodePasswordService(private val messageDigest: MessageDigest): EncodePasswordUseCase
{
    private val encoder = Base64.getEncoder()

    override fun invoke(plainPassword: String): String
    {
        return encoder.encodeToString(messageDigest.digest(plainPassword.encodeToByteArray()))
    }
}
