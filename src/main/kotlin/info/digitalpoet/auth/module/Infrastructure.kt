package info.digitalpoet.auth.module

import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.dsl.module
import org.litote.kmongo.KMongo

fun infrastructureModule(): Module
{
    return module(createdAtStart = true) {

        single {
            val mongoProperties = get<Application>().environment.config.config("mongodb")

            KMongo.createClient(mongoProperties.property("url").getString())
        }

    }
}