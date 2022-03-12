package info.digitalpoet.auth.utils

import java.util.UUID

object ID {
    fun random(): String = UUID.randomUUID().toString()
}
