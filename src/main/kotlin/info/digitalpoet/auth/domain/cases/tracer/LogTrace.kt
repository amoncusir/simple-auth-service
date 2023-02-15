package info.digitalpoet.auth.domain.cases.tracer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper

interface LogTrace
{
    companion object {
        const val NONE_PRIORITY = Int.MIN_VALUE
        const val LOW_PRIORITY = 0
        const val MEDIUM_PRIORITY = 1_000
        const val HIGH_PRIORITY = 1_000_000
        const val CRITICAL_PRIORITY = Int.MAX_VALUE
    }

    operator fun invoke(priority: Int, eventType: String, message: String, vararg additionalData: Any = arrayOf())

    fun none(eventType: String, message: String, vararg additionalData: Any = arrayOf()) =
        this(NONE_PRIORITY, eventType, message, *additionalData)

    fun low(eventType: String, message: String, vararg additionalData: Any = arrayOf()) =
        this(LOW_PRIORITY, eventType, message, *additionalData)

    fun medium(eventType: String, message: String, vararg additionalData: Any = arrayOf()) =
        this(MEDIUM_PRIORITY, eventType, message, *additionalData)

    fun high(eventType: String, message: String, vararg additionalData: Any = arrayOf()) =
        this(HIGH_PRIORITY, eventType, message, *additionalData)

    fun critical(eventType: String, message: String, vararg additionalData: Any = arrayOf()) =
        this(CRITICAL_PRIORITY, eventType, message, *additionalData)
}

class LogTraceAppender(vararg tracer: LogTrace): LogTrace
{
    private val tracers: Array<out LogTrace> = tracer
    override fun invoke(priority: Int, eventType: String, message: String, vararg additionalData: Any) {
        tracers.forEach { it(priority, eventType, message, *additionalData) }
    }
}

class PrintLogTracer(mapper: ObjectMapper? = null): LogTrace
{
    private val jsonMapper = mapper ?: JsonMapper.builder()
        .findAndAddModules()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .build()

    override fun invoke(priority: Int, eventType: String, message: String, vararg additionalData: Any) {
        println("[$priority] - **$eventType** :: $message")

        if (additionalData.isNotEmpty()) {
            additionalData.forEach {
                val json = jsonMapper.writeValueAsString(it)
                    .split("\n")
                    .joinToString("\n") { line -> "|-- $line" }
                println("|>> $it")
                println(json)
            }
            println("|<<")
        }
    }
}
