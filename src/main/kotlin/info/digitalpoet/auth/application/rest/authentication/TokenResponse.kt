package info.digitalpoet.auth.application.rest.authentication

import info.digitalpoet.auth.domain.command.token.TokenBuilder
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val token: String,
    val refreshToken: String?
) {
    constructor(from: TokenBuilder.Response): this(
        from.token,
        from.refreshToken?.toString()
    )
}

fun TokenBuilder.Response.toResponse() = TokenResponse(this)