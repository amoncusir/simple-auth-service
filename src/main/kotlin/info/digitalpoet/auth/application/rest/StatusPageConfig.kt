package info.digitalpoet.auth.application.rest

import info.digitalpoet.auth.domain.service.InvalidAuthentication
import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*


fun StatusPagesConfig.errorMapping() {

    exception<InvalidAuthentication> { call, _ -> call.respond(HttpStatusCode.Unauthorized, "Unauthorized") }

    exception<Throwable> { call, cause ->
        call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
    }
}