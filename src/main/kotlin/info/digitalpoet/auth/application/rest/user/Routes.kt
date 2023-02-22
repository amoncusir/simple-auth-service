package info.digitalpoet.auth.application.rest.user

import io.ktor.server.auth.*
import io.ktor.server.routing.*


fun Route.userRoutes() {

    createUser()
    getUser()
    userSessions()
}
