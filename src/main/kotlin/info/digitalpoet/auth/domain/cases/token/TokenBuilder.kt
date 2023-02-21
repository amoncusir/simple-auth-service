package info.digitalpoet.auth.domain.cases.token

import info.digitalpoet.auth.domain.model.Authentication

interface TokenBuilder
{
    data class TokenResponse(
        val token: String,
        val refreshToken: String?
    )

    operator fun invoke(authentication: Authentication): TokenResponse
}
