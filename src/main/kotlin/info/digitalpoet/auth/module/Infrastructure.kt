package info.digitalpoet.auth.module

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.dsl.module
import org.litote.kmongo.KMongo

fun infrastructureModule(): Module
{
    return module(createdAtStart = true) {

        single<MongoClient> {
            val mongoProperties = get<Application>().environment.config.config("mongodb")

            KMongo.createClient(mongoProperties.property("url").getString())
        }

        single<MongoDatabase> {
            val mongoProperties = get<Application>().environment.config.config("mongodb")
            val databaseName = mongoProperties.property("database").getString()

            get<MongoClient>().getDatabase(databaseName)
        }
    }
}