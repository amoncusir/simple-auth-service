package info.digitalpoet.auth.application.rest.user

import io.ktor.routing.Route
import io.ktor.routing.route

fun Route.userRoutes() {

    createUser()
    getUser()

    route("/sessions") {
        userSessions()
    }

}
