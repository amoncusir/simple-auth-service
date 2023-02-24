package info.digitalpoet.auth.domain.command.password

interface EncodePassword
{
    operator fun invoke(plainPassword: CharArray): String
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
