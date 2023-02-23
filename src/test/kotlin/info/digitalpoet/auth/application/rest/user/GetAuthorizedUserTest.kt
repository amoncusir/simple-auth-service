package info.digitalpoet.auth.application.rest.user

import com.typesafe.config.ConfigFactory
import info.digitalpoet.auth.ApplicationEngineTest
import info.digitalpoet.auth.application.rest.requestAuthenticationToken
import info.digitalpoet.auth.createTestApplicationWithConfig
import info.digitalpoet.auth.domain.command.user.CreateUser
import info.digitalpoet.auth.domain.command.user.UpdateUserPolicies
import info.digitalpoet.auth.domain.model.Policy
import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.module
import io.kjson.test.JSONExpect
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.koin.ktor.ext.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetAuthorizedUserTest: ApplicationEngineTest() {

    override val engine = createTestApplicationWithConfig(HoconApplicationConfig(ConfigFactory.load("test.conf"))) {

        module()

        get<CreateUser>().apply {
            this(CreateUser.Request("test@test.test", "test".toCharArray()))
            this(CreateUser.Request("admin@test.test", "test".toCharArray()))
        }

        get<UpdateUserPolicies>().apply {
            this(Email("admin@test.test"), listOf(Policy.buildWildcard("auth")))
        }
    }

    @Test
    fun `login with test user and try to get own information`() {
        engine.apply {

            val token = requestAuthenticationToken()

            println(token.token)

            handleRequest(HttpMethod.Get, "/user") {
                addHeader(HttpHeaders.Authorization, token.toHeader())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)

                JSONExpect.expectJSON(response.content!!) {
                    property("user") {
                        property("email") {
                            assertEquals("test@test.test", node)
                        }
                    }
                }
            }
        }
    }
}