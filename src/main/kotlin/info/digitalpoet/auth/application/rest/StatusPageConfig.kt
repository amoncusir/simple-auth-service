package info.digitalpoet.auth.application.rest

import info.digitalpoet.auth.domain.InvalidAuthentication
import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*


fun StatusPagesConfig.errorMapping() {

    exception<InvalidAuthentication> { call, _ -> call.respond(HttpStatusCode.Unauthorized) }

    exception<Throwable> { call, cause ->
        call.respond(HttpStatusCode.InternalServerError)
        call.application.environment.log.error("Unexpected Error", cause)
    }
}