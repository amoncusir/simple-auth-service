package info.digitalpoet.auth.domain.service

import info.digitalpoet.auth.domain.model.Authentication

interface TokenService
{
    data class TokenResponse(
        val token: String,
        val refreshToken: String?
    )

    fun buildToken(authentication: Authentication): TokenResponse
}
