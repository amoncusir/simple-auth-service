package info.digitalpoet.auth.application.rest.authentication

import com.typesafe.config.ConfigFactory
import info.digitalpoet.auth.ApplicationEngineTest
import info.digitalpoet.auth.application.rest.restTesting
import info.digitalpoet.auth.createTestApplicationWithConfig
import info.digitalpoet.auth.domain.service.UserService
import io.kjson.test.JSONExpect.Companion.expectJSON
import io.ktor.config.HoconApplicationConfig
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.formUrlEncode
import io.ktor.server.testing.handleRequest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.koin.ktor.ext.get
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RequestAuthenticationTest: ApplicationEngineTest()
{
    override val engine = createTestApplicationWithConfig(HoconApplicationConfig(ConfigFactory.load("test.conf"))) {

        restTesting()

        get<UserService>().apply {
            createUser(UserService.CreateUser("test@test.test", "test"))
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `authenticate user in get query parameters`(requestRefreshToken: Boolean = false)
    {
        engine.apply {
            val queryParams = mutableListOf<Pair<String, String?>>(
                "email" to "test@test.test",
                "password" to "test",
                "scope" to "auth"
            )
                .apply { if (requestRefreshToken) this.add("refresh" to null) }
                .formUrlEncode()

            handleRequest(HttpMethod.Get, "/authentication/testClient/basic?${queryParams}").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                expectJSON(response.content!!) {
                    property("tokens") {
                        property("token") {
                            assertNotNull(node)
                        }

                        property("refreshToken") {
                            if (requestRefreshToken) assertNotNull(node) else assertNull(node)
                        }
                    }
                }
            }
        }
    }
}
