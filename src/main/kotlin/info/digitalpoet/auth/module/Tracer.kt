package info.digitalpoet.auth.module

import info.digitalpoet.auth.domain.cases.tracer.LogTrace
import info.digitalpoet.auth.domain.cases.tracer.LogTraceAppender
import info.digitalpoet.auth.domain.cases.tracer.PrintLogTracer
import org.koin.core.module.Module
import org.koin.dsl.module

fun tracerModule(): Module
{
    return module(createdAtStart = true) {
        single<LogTrace> { LogTraceAppender(
            PrintLogTracer()
        ) }
    }
}