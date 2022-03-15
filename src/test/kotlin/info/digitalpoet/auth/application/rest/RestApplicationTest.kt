package info.digitalpoet.auth.application.rest

import info.digitalpoet.auth.module.serviceModule
import info.digitalpoet.auth.modules.testInMemoryRepositories
import info.digitalpoet.auth.plugins
import io.ktor.application.Application
import io.ktor.application.install
import org.koin.dsl.module
import org.koin.ktor.ext.Koin

fun Application.restTesting()
{
    plugins()

    install(Koin) {

        val ktorModule = module(createdAtStart = true) {
            single { this@restTesting }
        }

        modules(
            ktorModule,
            testInMemoryRepositories(),
            serviceModule()
        )
    }
}
