package info.digitalpoet.auth.domain.command.token

import info.digitalpoet.auth.domain.model.Authentication

interface TokenBuilder
{
    data class Response(
        val token: String,
        val refreshToken: String?
    )

    operator fun invoke(authentication: Authentication): Response
}
