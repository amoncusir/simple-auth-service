package info.digitalpoet.auth.plugins.security

import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.authenticateSelf(build: Route.() -> Unit) = authenticate(
    "self", strategy = AuthenticationStrategy.Required, build = build
)

fun Route.authenticateAdmin(build: Route.() -> Unit) = authenticate(
    "admin", strategy = AuthenticationStrategy.Required, build = build
)

fun Route.authenticateService(build: Route.() -> Unit) = authenticate(
    "service", strategy = AuthenticationStrategy.Required, build = build
)
