package info.digitalpoet.auth.domain.entity

import info.digitalpoet.auth.domain.model.AuthenticationScope
import io.ktor.server.auth.*
import java.time.Instant

data class Token(
    val userId: String,
    val scope: List<AuthenticationScope>,
    /** ID of the client that request the authentication */
    val client: String,
    /** Time to live: Date unit the token was valid */
    val ttl: Instant,
): Principal
{
    fun hasService(service: String): Boolean {
        return scope.any { it.service == service }
    }

    fun hasServiceWithGrants(service: String, vararg grants: String): Boolean {
        val scopeGrants = scope.firstOrNull { it.service == service }?.grant ?: return false

        if (scopeGrants.any { it == "*" }) return true

        return scopeGrants.containsAll(grants.toList())
    }
}
