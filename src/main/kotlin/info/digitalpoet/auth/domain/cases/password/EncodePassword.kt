package info.digitalpoet.auth.domain.cases.password

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Helper
import java.nio.CharBuffer
import java.security.MessageDigest
import java.util.*

interface EncodePasswordUseCase
{
    operator fun invoke(plainPassword: CharArray): String
}

class B64EncodePasswordService(private val messageDigest: MessageDigest): EncodePasswordUseCase
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
): EncodePasswordUseCase
{
    override fun invoke(plainPassword: CharArray): String
    {
        return argon.hash(plainPassword)
    }
}
