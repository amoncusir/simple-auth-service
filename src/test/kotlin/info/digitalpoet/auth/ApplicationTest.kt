package info.digitalpoet.auth

import io.ktor.application.Application
import io.ktor.config.ApplicationConfig
import io.ktor.server.engine.ApplicationEngineEnvironmentBuilder
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withApplication

fun <R> createTestApplication(
    moduleFunction: Application.() -> Unit,
    configure: ApplicationEngineEnvironmentBuilder.() -> Unit = {},
    test: TestApplicationEngine.() -> R): R
{
    return withApplication(applicationEngineEnvironment(configure)) {
        moduleFunction(application)
        test()
    }
}

fun <R> createTestApplicationWithConfig(
    applicationConfig: ApplicationConfig,
    moduleFunction: Application.() -> Unit,
    test: TestApplicationEngine.() -> R): R
{
    return createTestApplication(moduleFunction, { config = applicationConfig }, test)
}
