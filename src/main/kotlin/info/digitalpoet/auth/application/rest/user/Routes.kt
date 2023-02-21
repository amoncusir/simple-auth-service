package info.digitalpoet.auth.application.rest.user

import io.ktor.server.auth.*
import io.ktor.server.routing.*


fun Route.userRoutes() {

    authenticate("admin") {
        createUser()
    }

    authenticate("self") {
        getUser()
        route("/sessions") {
            userSessions()
        }
    }
}
