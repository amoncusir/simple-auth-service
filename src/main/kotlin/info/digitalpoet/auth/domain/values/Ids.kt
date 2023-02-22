package info.digitalpoet.auth.domain.values

import java.security.SecureRandom
import java.util.*

@JvmInline
value class UserId(private val id: String)
{
    companion object {
        fun new(): UserId = UserId(UUID.randomUUID().toString())
    }

    override fun toString() = id
}

@JvmInline
value class RefreshId(private val id: String)
{
    companion object {

        private val secureRandom = SecureRandom.getInstance("SHA1PRNG")
        private val encoder = Base64.getUrlEncoder().withoutPadding()
        fun new(): RefreshId
        {
            val bytes = ByteArray(64)
            secureRandom.nextBytes(bytes)
            return RefreshId(encoder.encodeToString(bytes))
        }
    }

    override fun toString() = id
}

@JvmInline
value class Email(private val email: String)
{
    override fun toString() = email
}