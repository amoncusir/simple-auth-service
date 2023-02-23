package info.digitalpoet.auth.application.rest.authentication

import com.typesafe.config.ConfigFactory
import info.digitalpoet.auth.ApplicationEngineTest
import info.digitalpoet.auth.application.rest.requestAuthenticationToken
import info.digitalpoet.auth.application.rest.requestAuthenticationTokenRefresh
import info.digitalpoet.auth.createTestApplicationWithConfig
import info.digitalpoet.auth.domain.command.user.CreateUser
import info.digitalpoet.auth.module
import info.digitalpoet.auth.testUser
import io.kjson.test.JSONExpect.Companion.expectJSON
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.junit.jupiter.params.provider.ValueSource
import org.koin.ktor.ext.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RefreshAuthenticationTest: ApplicationEngineTest()
{
    override val engine = createTestApplicationWithConfig(HoconApplicationConfig(ConfigFactory.load("test.conf"))) {

        module()

        get<CreateUser>().apply {
            testUser()
        }
    }

    @Test
    fun `login with refresh token and request for new token`()
    {
        engine.apply {
            val token = requestAuthenticationToken(refresh = true)

            handleRequest(HttpMethod.Get, "/user") {
                addHeader(HttpHeaders.Authorization, token.toHeader())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)

                expectJSON(response.content!!) {
                    property("user") {
                        property("email") {
                            assertEquals("test@test.test", node)
                        }
                    }
                }
            }

            val refreshedToken = requestAuthenticationTokenRefresh(token.refreshToken!!)

            handleRequest(HttpMethod.Get, "/user") {
                addHeader(HttpHeaders.Authorization, refreshedToken.toHeader())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)

                expectJSON(response.content!!) {
                    property("user") {
                        property("email") {
                            assertEquals("test@test.test", node)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `login with refresh token and request for new must be fail if twice`()
    {
        engine.apply {
            val token = requestAuthenticationToken(refresh = true)

            handleRequest(HttpMethod.Get, "/authentication/refresh/${token.refreshToken}") {
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
                                assertNotNull(node)
                            }
                        }
                    }
            }

            handleRequest(HttpMethod.Get, "/authentication/refresh/${token.refreshToken}") {
                addHeader("Content-Type", "application/json; charset=utf-8")
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `login with refresh token and request for new must be fail if is expired`()
    {
        engine.apply {

            val jwtTtl = application.environment.config.property("jwt.ttl").getString().toLong()
            val refreshTtl = application.environment.config.property("jwt.refresh-ttl-plus").getString().toLong()

            val token = requestAuthenticationToken(refresh = true)

            Thread.sleep((jwtTtl + refreshTtl) * 1000)

            handleRequest(HttpMethod.Get, "/authentication/refresh/${token.refreshToken}") {
                addHeader("Content-Type", "application/json; charset=utf-8")
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `login with refresh token must be fail if is expired`()
    {
        engine.apply {

            val jwtTtl = application.environment.config.property("jwt.ttl").getString().toLong()

            val token = requestAuthenticationToken(refresh = false)

            Thread.sleep((jwtTtl * 1000) + 1000)

            handleRequest(HttpMethod.Get, "/user") {
                addHeader(HttpHeaders.Authorization, token.toHeader())
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }
}
