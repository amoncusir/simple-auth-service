package info.digitalpoet.auth.plugins


import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization()
{
    install(ContentNegotiation) {

        checkAcceptHeaderCompliance = true

        jackson {
            registerKotlinModule()
        }
    }
}
