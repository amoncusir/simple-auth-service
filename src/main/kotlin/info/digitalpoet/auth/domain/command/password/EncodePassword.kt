package info.digitalpoet.auth.domain.command.password

import java.nio.CharBuffer
import java.security.MessageDigest
import java.util.*

interface EncodePassword
{
    operator fun invoke(plainPassword: CharArray): String
}

class B64EncodePasswordService(private val messageDigest: MessageDigest): EncodePassword
{
    private val encoder = Base64.getEncoder()

    private val charset = Charsets.UTF_8

    override fun invoke(plainPassword: CharArray): String
    {
        val byteBuffer = charset.encode(CharBuffer.wrap(plainPassword))
        return encoder.encodeToString(messageDigest.digest(byteBuffer.array()))
    }
}

class Argon2EncodePasswordService(
    private val argon: Argon2Wrapper
): EncodePassword
{
    override fun invoke(plainPassword: CharArray): String
    {
        return argon.hash(plainPassword)
    }
}
