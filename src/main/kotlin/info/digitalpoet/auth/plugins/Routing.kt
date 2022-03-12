package info.digitalpoet.auth.plugins

import info.digitalpoet.auth.application.rest.registerControllers
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.routing.routing

fun Application.configureRouting()
{
    install(CORS) {
        allowCredentials = true
        allowSameOrigin = true
        maxAgeInSeconds = 3600

        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)

        header(HttpHeaders.ContentType)
        header(HttpHeaders.Authorization)

        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    routing {
        registerControllers()
    }
}
