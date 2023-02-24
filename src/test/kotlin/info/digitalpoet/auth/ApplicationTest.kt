package info.digitalpoet.auth

import info.digitalpoet.auth.domain.command.user.CreateUser
import info.digitalpoet.auth.domain.repository.Repository
import info.digitalpoet.auth.module.getRepository
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.koin.ktor.ext.get

fun CreateUser.testUser(email: String = "test@test.test") = this(CreateUser.Request(email, "test".toCharArray()))

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
    abstract val engineFactory: () -> TestApplicationEngine

    lateinit var engine: TestApplicationEngine

    val application: Application
        get() = engine.application

    inline fun <reified T: Any> get() = application.get<T>()

    inline fun <reified T: Repository> getRepo() = application.getRepository<T>()

    @BeforeAll
    fun startEngine() {
        println("Start")

        if (!this::engine.isInitialized) engine = engineFactory()

        engine.start()
    }

    @AfterAll
    fun stopEngine() {
        println("Stop")
        engine.stop(0L, 0L)
    }
}
