package info.digitalpoet.auth.domain.cases.tracer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper

interface EventPublisher
{
    operator fun invoke(channel: String, data: Any)
}

class EventPublisherAppender(private val domain: String, vararg tracer: EventPublisher): EventPublisher
{
    private val tracers: Array<out EventPublisher> = tracer

    override fun invoke(channel: String, data: Any) {
        tracers.forEach { it("$domain.$channel", data) }
    }
}

class PrintEventPublisher(mapper: ObjectMapper? = null): EventPublisher
{
    private val jsonMapper = mapper ?: JsonMapper.builder()
        .findAndAddModules()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .build()

    override fun invoke(channel: String, data: Any) {
        println("New Event in channel :: $channel")

        println("|>> $data")

        if (data !is CharSequence && data !is Number && data !is Boolean)
        {
            println(
                jsonMapper.writeValueAsString(data)
                    .split("\n")
                    .joinToString("\n") { line -> "|-- $line" }
            )
        }

        println("|<<")
    }
}
