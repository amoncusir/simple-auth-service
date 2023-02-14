package info.digitalpoet.auth.plugins

import info.digitalpoet.auth.application.rest.errorMapping
import info.digitalpoet.auth.application.rest.registerControllers
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
//    install(CORS) {
//        allowCredentials = true
//        allowSameOrigin = true
//        maxAgeInSeconds = 3600
//
//        method(HttpMethod.Options)
//        method(HttpMethod.Put)
//        method(HttpMethod.Delete)
//        method(HttpMethod.Patch)
//
//        header(HttpHeaders.ContentType)
//        header(HttpHeaders.Authorization)
//
//        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
//    }

    install(StatusPages) {
        errorMapping()
    }

    routing {
        registerControllers()
    }
}
