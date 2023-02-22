package info.digitalpoet.auth.application.rest

import info.digitalpoet.auth.application.rest.authentication.authenticationRoutes
import info.digitalpoet.auth.application.rest.user.userRoutes
import io.ktor.server.routing.*

fun Route.registerControllers() {

    userRoutes()
    authenticationRoutes()
}
