package info.digitalpoet.auth

import info.digitalpoet.auth.module.eventsModule
import info.digitalpoet.auth.module.repositoryModule
import info.digitalpoet.auth.module.serviceModule
import info.digitalpoet.auth.plugins.configureRouting
import info.digitalpoet.auth.plugins.configureSecurity
import info.digitalpoet.auth.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

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
    val applicationModule = this

    plugins()

    install(Koin) {
        val ktorModule = module(createdAtStart = true) {
            single<Application> { applicationModule }
        }

        modules(
            ktorModule,
            eventsModule(),
            repositoryModule(),
            serviceModule()
        )
    }
}
