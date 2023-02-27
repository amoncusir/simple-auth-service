package info.digitalpoet.auth.domain.model

import info.digitalpoet.auth.domain.values.RefreshId
import java.time.Instant
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDateTime

data class AuthenticationScope(
    val service: String,
    val grant: Set<String>
)

data class Authentication(
    /** Granted User */
    val user: User,
    /**
     * User scope list. Define the user's access over services. Example:
     * ```json4
     * [
     *   { service: '*', grant: 'read' }, // Can read all services
     *   { service: 'admin', grant: 'update' }, // Can update admin service
     *   { service: 'admin', grant: ['read', 'update'] }, // Can update admin service
     *   { service: 'photos', grant: '*' }, // Can do anything on photos service
     * ]
     * ```
     */
    val scope: List<AuthenticationScope>,
    /** ID of the client that request the authentication */
    val client: String,
    /** Time to live: Date unit the token was valid */
    val ttl: LocalDateTime,
    /**
     * Token refresh ID to request a valid token with the same configuration.
     * If it's null, can't be request a new token.
     */
    val refreshId: RefreshId?
) {

    companion object {

        fun with(refresh: Boolean, user: User, client: String, plusTtl: Long, scope: List<AuthenticationScope>) =
            Authentication(if (refresh) RefreshId.new() else null, user, client, plusTtl, scope)

        fun withRefresh(user: User, client: String, plusTtl: Long, scope: List<AuthenticationScope>) =
            Authentication(RefreshId.new(), user, client, plusTtl, scope)

        fun withoutRefresh(user: User, client: String, plusTtl: Long, scope: List<AuthenticationScope>) =
            Authentication(null, user, client, plusTtl, scope)
    }

    constructor(refreshId: RefreshId?, user: User, client: String, plusTtl: Long, scope: List<AuthenticationScope>):
            this(user, scope, client, LocalDateTime.now().plusSeconds(plusTtl), refreshId)

    fun newAuth(refreshId: RefreshId, ttl: LocalDateTime): Authentication {
        return copy(ttl = ttl, refreshId = refreshId)
    }

    fun newAuth(ttl: LocalDateTime): Authentication {
        return copy(ttl = ttl, refreshId = RefreshId.new())
    }

    fun newAuth(ttl: Long): Authentication {
        return newAuth(ttl = LocalDateTime.now().plusSeconds(ttl))
    }

    fun isExpired(compare: ChronoLocalDateTime<*>? = null): Boolean =
        ttl.isBefore(compare ?: LocalDateTime.now())
}
