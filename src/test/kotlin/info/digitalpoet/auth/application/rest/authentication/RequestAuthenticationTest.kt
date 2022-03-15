package info.digitalpoet.auth.application.rest.authentication

import com.typesafe.config.ConfigFactory
import info.digitalpoet.auth.application.rest.restTesting
import info.digitalpoet.auth.createTestApplicationWithConfig
import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.service.UserService
import io.ktor.application.Application
import io.ktor.config.HoconApplicationConfig
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.expectHttpBody
import io.ktor.http.formUrlEncode
import io.ktor.server.testing.handleRequest
import net.pwall.json.test.JSONExpect
import net.pwall.json.test.JSONExpect.Companion.expectJSON
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.koin.ktor.ext.getKoin
import org.koin.ktor.ext.inject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RequestAuthenticationTest
{
    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `authenticate user in get query parameters`(requestRefreshToken: Boolean)
    {
        createTestApplicationWithConfig(
            HoconApplicationConfig(ConfigFactory.load("test.conf")), {
                restTesting()

                val userService by inject<UserService>()

                userService.createUser(UserService.CreateUser("test@test.test", "test"))
            }
        ) {
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
