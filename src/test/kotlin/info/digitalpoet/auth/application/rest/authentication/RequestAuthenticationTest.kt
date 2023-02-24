package info.digitalpoet.auth.application.rest.authentication

import com.typesafe.config.ConfigFactory
import info.digitalpoet.auth.ApplicationEngineTest
import info.digitalpoet.auth.createTestApplicationWithConfig
import info.digitalpoet.auth.domain.command.user.CreateUser
import info.digitalpoet.auth.domain.command.user.UpdateUserStatus
import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.extension.WithMongoDBContainer
import info.digitalpoet.auth.module
import info.digitalpoet.auth.testUser
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

@WithMongoDBContainer
class RequestAuthenticationTest: ApplicationEngineTest()
{
    override val engineFactory = {
        createTestApplicationWithConfig(HoconApplicationConfig(ConfigFactory.load("test.conf"))) {

            module()

            get<CreateUser>().apply {
                testUser()
                testUser("admin@test.test")
                testUser("invalid@test.test")
            }

            get<UpdateUserStatus>().apply {
                this(Email("invalid@test.test"), false)
            }
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

                println(response.content)

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
                        "scope" : { "auth": ["self"] },
                        "refresh" : $requestRefreshToken
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
                        "scope" : { "auth": ["self"] },
                        "refresh" : false
                    }
                """.trimIndent())

                addHeader("Content-Type", "application/json; charset=utf-8")
            }
                .apply {
                    assertEquals(HttpStatusCode.Unauthorized, response.status())
                }
        }
    }

    @Test
    fun `invalid user must return unauthorized error code`()
    {
        engine.apply {
            handleRequest(HttpMethod.Post, "/authentication/testClient/basic") {
                setBody("""
                    {
                        "email" : "invalid@test.test",
                        "password" : "test",
                        "scope" : { "auth": ["self"] },
                        "refresh" : false
                    }
                """.trimIndent())

                addHeader("Content-Type", "application/json; charset=utf-8")
            }
                .apply {
                    assertEquals(HttpStatusCode.Unauthorized, response.status())
                }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["*", "admin", "admin,self", "writer", "*,self", "reader,*", "reader,admin"])
    fun `invalid scope must return unauthorized error code`(scope: String)
    {
        val formattedScope = scope.split(",").joinToString(prefix = "\"", postfix = "\"")

        engine.apply {
            handleRequest(HttpMethod.Post, "/authentication/testClient/basic") {
                setBody("""
                    {
                        "email" : "policy@test.test",
                        "password" : "test",
                        "scope" : { "auth": [$formattedScope] },
                        "refresh" : false
                    }
                """.trimIndent())

                addHeader("Content-Type", "application/json; charset=utf-8")
            }
                .apply {
                    assertEquals(HttpStatusCode.Unauthorized, response.status())
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
