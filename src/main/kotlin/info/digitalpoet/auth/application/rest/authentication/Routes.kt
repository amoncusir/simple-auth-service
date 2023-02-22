package info.digitalpoet.auth.application.rest.authentication

import io.ktor.server.routing.*

fun Route.authenticationRoutes() {

    refreshToken()
    basicRequestAuthentication()
}
