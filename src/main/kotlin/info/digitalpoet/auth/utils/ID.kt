package info.digitalpoet.auth.utils

import java.util.*

object ID {
    fun random(): String = UUID.randomUUID().toString()
}
