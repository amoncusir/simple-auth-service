package info.digitalpoet.auth.application.rest.authentication

import io.ktor.routing.Route
import io.ktor.routing.route

fun Route.authenticationRoutes() {

    refreshToken()

    route("/{clientId}") {

        route("/basic") {
            basicRequestAuthentication()
        }

    }
}
