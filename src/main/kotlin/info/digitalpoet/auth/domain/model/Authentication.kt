package info.digitalpoet.auth.domain.model

import info.digitalpoet.auth.domain.values.RefreshId
import java.time.Instant

data class AuthenticationScope(
    val service: String,
    val grant: List<String>
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
    val ttl: Instant,
    /**
     * Token refresh ID to request a valid token with the same configuration.
     * If it's null, can't be request a new token.
     */
    val refreshId: RefreshId?
) {
    fun newAuth(refreshId: RefreshId?, ttl: Instant): Authentication {
        return copy(ttl = ttl, refreshId = refreshId)
    }
}
