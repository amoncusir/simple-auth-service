package info.digitalpoet.auth.plugins.security

import io.ktor.server.auth.*
import io.ktor.server.routing.*

enum class AuthenticationProfile(val profile: String)
{
    SELF("self"),
    ADMIN("admin"),
    SERVICE("service")
}

fun Route.authenticateSelf(build: Route.() -> Unit) = authenticateWith(AuthenticationProfile.SELF, build = build)

fun Route.authenticateAdmin(build: Route.() -> Unit)  = authenticateWith(AuthenticationProfile.ADMIN, build = build)

fun Route.authenticateService(build: Route.() -> Unit) = authenticateWith(AuthenticationProfile.SERVICE, build = build)

fun Route.authenticateWith(vararg profile: AuthenticationProfile, build: Route.() -> Unit) = authenticate(
    *(profile.map { it.profile }.toTypedArray()), strategy = AuthenticationStrategy.Required, build = build
)