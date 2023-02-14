package info.digitalpoet.auth.domain.service

import info.digitalpoet.auth.domain.model.Authentication

interface UserAuthenticationService
{
    class AuthenticationRequest(
        val email: String,
        val rawPassword: CharArray,
        val scope: Map<String, List<String>>,
        val clientId: String,
        val ttl: Long,
        val withRefresh: Boolean,
    )

    fun authenticateUser(request: AuthenticationRequest): Authentication

    fun authenticateUser(refreshId: String): Authentication
}
