package info.digitalpoet.auth.domain.command.password

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Helper

class Argon2Wrapper(
    private val argon: Argon2,
    private val memory: Int = 256 * 1024,
    private val parallelism: Int = 4,
    private val iterations: Int = Argon2Helper.findIterations(argon, 2000, memory, parallelism)
) {
    private val charset = Charsets.UTF_8

    fun hash(password: CharArray): String
    {
        return try {
            argon.hash(iterations, memory, parallelism, password, charset)
        } finally {
            argon.wipeArray(password)
        }
    }

    fun validate(hash: String, password: CharArray): Boolean
    {
        return try {
            argon.verify(hash, password, charset)
        } finally {
            argon.wipeArray(password)
        }
    }
}