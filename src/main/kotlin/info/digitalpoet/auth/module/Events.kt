package info.digitalpoet.auth.module

import info.digitalpoet.auth.domain.cases.tracer.EventPublisher
import info.digitalpoet.auth.domain.cases.tracer.EventPublisherAppender
import info.digitalpoet.auth.domain.cases.tracer.PrintEventPublisher
import org.koin.core.module.Module
import org.koin.dsl.module

fun eventsModule(): Module
{
    return module(createdAtStart = true) {
        single<EventPublisher> { EventPublisherAppender(domain = "auth",
            PrintEventPublisher()
        ) }
    }
}