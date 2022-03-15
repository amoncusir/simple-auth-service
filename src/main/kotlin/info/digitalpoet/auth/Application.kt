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
import org.koin.dsl.module
import org.koin.ktor.ext.Koin

fun main(args: Array<String>)
{
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

fun Application.plugins()
{
    configureSerialization()
    configureSecurity()
    configureRouting()
}

fun Application.module()
{
    plugins()

    install(Koin) {
        val ktorModule = module(createdAtStart = true) {
            single { this@module }
        }

        modules(
            ktorModule,
            repositoryModule(),
            serviceModule()
        )
    }
}
