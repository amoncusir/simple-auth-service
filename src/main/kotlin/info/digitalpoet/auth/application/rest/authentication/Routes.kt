package info.digitalpoet.auth.application.rest.authentication

import io.ktor.server.routing.*

fun Route.authenticationRoutes() {

    route("/refresh") {
        refreshToken()
    }

    route("/{clientId}") {

        route("/basic") {
            basicRequestAuthentication()
        }
    }
}
