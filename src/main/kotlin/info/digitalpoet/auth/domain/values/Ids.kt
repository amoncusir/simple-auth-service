package info.digitalpoet.auth.domain.values

import java.util.UUID

@JvmInline
value class UserId(private val id: String)
{
    companion object {
        fun new(): UserId = UserId(UUID.randomUUID().toString())
    }

    override fun toString() = id
}

@JvmInline
value class Email(private val email: String)
{
    override fun toString() = email
}