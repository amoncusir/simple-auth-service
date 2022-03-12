package info.digitalpoet.auth.application.rest

import info.digitalpoet.auth.application.rest.authentication.authenticationRoutes
import info.digitalpoet.auth.application.rest.user.userRoutes
import io.ktor.auth.authenticate
import io.ktor.routing.Route
import io.ktor.routing.route

fun Route.registerControllers() {

    authenticate {
        route("/user") {
            userRoutes()
        }
    }

    route("/authentication") {
        authenticationRoutes()
    }
}
