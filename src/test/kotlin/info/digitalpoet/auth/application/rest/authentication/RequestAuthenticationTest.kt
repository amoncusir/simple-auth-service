package info.digitalpoet.auth.application.rest.authentication

import com.typesafe.config.ConfigFactory
import info.digitalpoet.auth.ApplicationEngineTest
import info.digitalpoet.auth.createTestApplicationWithConfig
import info.digitalpoet.auth.domain.service.UserService
import info.digitalpoet.auth.module
import io.kjson.test.JSONExpect.Companion.expectJSON
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.koin.ktor.ext.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RequestAuthenticationTest: ApplicationEngineTest()
{
    override val engine = createTestApplicationWithConfig(HoconApplicationConfig(ConfigFactory.load("test.conf"))) {

        module()

        get<UserService>().apply {
            createUser(UserService.CreateUser("test@test.test", "test".toCharArray()))
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `authenticated user in get query parameters`(requestRefreshToken: Boolean = false)
    {
        engine.apply {
            val queryParams = mutableListOf<Pair<String, String?>>(
                "email" to "test@test.test",
                "password" to "test",
                "scope" to "auth"
            )
                .apply { if (requestRefreshToken) add("refresh" to null) }
                .formUrlEncode()

            handleRequest(HttpMethod.Get, "/authentication/testClient/basic?${queryParams}").apply {

                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)

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

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `authenticated user in post method`(requestRefreshToken: Boolean = false)
    {
        engine.apply {
            handleRequest(HttpMethod.Post, "/authentication/testClient/basic") {
                setBody("""
                    {
                        "email" : "test@test.test",
                        "password" : "test",
                        "scope" : { "auth": ["*"] },
                        "refresh" : "$requestRefreshToken"
                    }
                """.trimIndent())

                addHeader("Content-Type", "application/json; charset=utf-8")
            }
                .apply {
                    assertEquals(HttpStatusCode.OK, response.status())
                    assertNotNull(response.content)

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
    @Test
    fun `failed authentication must return unauthorized error code`()
    {
        engine.apply {
            handleRequest(HttpMethod.Post, "/authentication/testClient/basic") {
                setBody("""
                    {
                        "email" : "test@test.test",
                        "password" : "invalid_password",
                        "scope" : { "auth": ["*"] },
                        "refresh" : "false"
                    }
                """.trimIndent())

                addHeader("Content-Type", "application/json; charset=utf-8")
            }
                .apply {
                    assertEquals(HttpStatusCode.Unauthorized, response.status())
                    assertNotNull(response.content)
                }
        }
    }

    @Test
    fun `authenticate user in post method with invalid content-type`()
    {
        engine.apply {
            handleRequest(HttpMethod.Post, "/authentication/testClient/basic") {
                setBody("""
                    <fakeXml>
                        <username>fake</username>
                    </fakeXml>
                """.trimIndent())

                addHeader("Content-Type", "application/xml")
            }
                .apply {
                    assertEquals(HttpStatusCode.UnsupportedMediaType, response.status())
                    assertNull(response.content)
                }
        }
    }
}
