package info.digitalpoet.auth.application.rest

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.reflect.KClass

data class AuthenticationToken(
    val token: String,
    val refreshToken: String?
) {
    fun canRefreshed(): Boolean = refreshToken.isNullOrBlank()

    fun toHeader() = "Bearer $token"
}

object JsonSerializer {
    private val jsonMapper = ObjectMapper()
        .registerKotlinModule()

    fun toNode(json: String): JsonNode = jsonMapper.readTree(json)

    fun <T> toObject(node: JsonNode, type: Class<T>): T = jsonMapper.readValue(node.traverse(), type)

    fun <T> toObject(json: String, type: Class<T>): T = jsonMapper.readValue(json, type)

    inline fun <reified T> toObjectWithRoot(rootKey: String, json: String): T
    {
        val node = toNode(json)
        return toObject(node.get(rootKey), T::class.java)
    }

    inline fun <reified T> toObject(json: String): T {
        return toObject(json, T::class.java)
    }
}

fun TestApplicationEngine.requestAuthenticationToken(
    email: String = "test@test.test",
    password: String = "test",
    scope: Map<String, Array<String>> = mapOf("auth" to arrayOf("self")),
    refresh: Boolean = false
): AuthenticationToken
{
    val serializedScope = scope
        .mapValues { it.value.joinToString(prefix = "\"", postfix = "\"") }
        .map { "\"${it.component1()}\": [${it.component2()}]" }
        .joinToString()

    val loginRequest: TestApplicationCall = handleRequest(HttpMethod.Post, "/authentication/testClient/basic") {
                setBody(
                    """
                    {
                        "email" : "$email",
                        "password" : "$password",
                        "scope" : { $serializedScope },
                        "refresh" : "$refresh"
                    }
                """.trimIndent()
                )

                addHeader("Content-Type", "application/json; charset=utf-8")
            }

    return JsonSerializer.toObjectWithRoot("tokens", loginRequest.response.content!!)
}