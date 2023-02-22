package info.digitalpoet.auth.domain.command.token

import info.digitalpoet.auth.domain.model.Authentication
import info.digitalpoet.auth.domain.values.RefreshId

interface TokenBuilder
{
    data class Response(
        val token: String,
        val refreshToken: RefreshId?
    )

    operator fun invoke(authentication: Authentication): Response
}
