package info.digitalpoet.auth

import info.digitalpoet.auth.module.repositoryModule
import info.digitalpoet.auth.module.serviceModule
import info.digitalpoet.auth.plugins.configureRouting
import info.digitalpoet.auth.plugins.configureSecurity
import info.digitalpoet.auth.plugins.configureSerialization
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>)
{
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

fun Application.module() {

    configureSerialization()
    configureSecurity()
    configureRouting()

    install(Koin) {
        val ktorModule = module(createdAtStart = true) {
            single { this@module }
        }

        //slf4jLogger(Level.ERROR)

        modules(
            ktorModule,
            repositoryModule(),
            serviceModule()
        )
    }
}
