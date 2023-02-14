package info.digitalpoet.auth

import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.engine.ApplicationEngineEnvironmentBuilder
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestEngine
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.withApplication
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.koin.ktor.ext.get

fun <R> withTestApplication(
    moduleFunction: Application.() -> Unit,
    configure: ApplicationEngineEnvironmentBuilder.() -> Unit = {},
    test: TestApplicationEngine.() -> R): R
{
    return withApplication(applicationEngineEnvironment(configure)) {
        moduleFunction(application)
        test()
    }
}

fun <R> withTestApplicationWithConfig(
    applicationConfig: ApplicationConfig,
    moduleFunction: Application.() -> Unit,
    test: TestApplicationEngine.() -> R): R
{
    return withTestApplication(moduleFunction, { config = applicationConfig }, test)
}

fun createTestApplicationWithConfig(
    environment: ApplicationEngineEnvironment = createTestEnvironment(),
    configure: TestApplicationEngine.Configuration.() -> Unit = {},
): TestApplicationEngine
{
    return TestEngine.create(environment, configure)
}

fun createTestApplicationWithConfig(
    applicationConfig: ApplicationConfig = MapApplicationConfig("ktor.deployment.environment" to "test"),
    moduleFunction: Application.() -> Unit = {}
    ): TestApplicationEngine
{
    val app = createTestApplicationWithConfig(applicationEngineEnvironment { config = applicationConfig })

    moduleFunction(app.application)

    return app
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ApplicationEngineTest
{
    abstract val engine: TestApplicationEngine

    val application: Application
        get() = engine.application

    inline fun <reified T: Any> get() = application.get<T>()

    @BeforeAll
    fun startEngine() {
        println("Start")
        engine.start()
    }

    @AfterAll
    fun stopEngine() {
        println("Stop")
        engine.stop(0L, 0L)
    }
}
