package info.digitalpoet.auth.domain.entity

import info.digitalpoet.auth.domain.model.AuthenticationScope
import io.ktor.auth.Principal
import java.time.Instant

data class Token(
    val userId: String,
    val scope: List<AuthenticationScope>,
    /** ID of the client that request the authentication */
    val client: String,
    /** Time to live: Date unit the token was valid */
    val ttl: Instant,
): Principal
