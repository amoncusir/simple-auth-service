package info.digitalpoet.auth.infrastrucutre.mongodb

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import com.typesafe.config.ConfigFactory
import info.digitalpoet.auth.ApplicationEngineTest
import info.digitalpoet.auth.createTestApplicationWithConfig
import info.digitalpoet.auth.domain.model.*
import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.repository.DuplicateEntity
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.domain.values.RefreshId
import info.digitalpoet.auth.domain.values.UserId
import info.digitalpoet.auth.extension.GenericStaticContainerExtension
import info.digitalpoet.auth.extension.WithMongoDBContainer
import info.digitalpoet.auth.infrastructure.mongodb.MongoDBAuthenticationRepository
import info.digitalpoet.auth.infrastructure.mongodb.MongoDBUserRepository
import info.digitalpoet.auth.koin
import info.digitalpoet.auth.module.eventsModule
import info.digitalpoet.auth.module.infrastructureModule
import info.digitalpoet.auth.module.repositoryModule
import io.ktor.server.config.*
import org.bson.Document
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@WithMongoDBContainer
@GenericStaticContainerExtension.Lifecycle(GenericStaticContainerExtension.Lifecycle.CleanUp.AFTER_METHOD)
class MongoDBAuthenticationRepositoryTest: ApplicationEngineTest()
{
    override val engineFactory = {
        createTestApplicationWithConfig(HoconApplicationConfig(ConfigFactory.load("test.conf"))) {
            koin(
                eventsModule(),
                infrastructureModule(),
                repositoryModule(),
            )
        }
    }

    private fun testUser() = User(
            UserId.new(),
            Email("test@test.test"),
            "hashhh",
            true,
            Policies(Policy("auth", "self"))
        )

    @Test
    fun `save authentication`()
    {
        val repository = getRepo<AuthenticationRepository>()
        val database = get<MongoDatabase>()

        val user = testUser()
        val auth = Authentication.withRefresh(user, "testClient", 10L, listOf())

        repository.save(auth)

        val collection = database.getCollection(MongoDBAuthenticationRepository.COLLECTION)
        val doc = collection.find(eq("_id", auth.refreshId.toString()))
            .first()

        assertNotNull(doc)
        assertEquals(auth.refreshId, RefreshId(doc.getString("_id")))
        assertEquals(auth.client, doc.getString("client"))
        assertEquals(auth.user.userId.toString(), (doc["user"] as Document).getString("_id"))
    }

    @Test
    fun `save authentication and delete by userId`()
    {
        val repository = getRepo<AuthenticationRepository>()
        val database = get<MongoDatabase>()

        val user = testUser()
        val auths = (1..5).map {
            Authentication.withRefresh(user, "testClient", 10L, listOf(AuthenticationScope("$it", setOf("*"))))
        }

        auths.forEach { repository.save(it) }

        val deletedAuths = repository.deleteByUserId(user.userId)

        val collection = database.getCollection(MongoDBAuthenticationRepository.COLLECTION)

        assertEquals(5, deletedAuths.size)

        val find = collection.find()
        assertEquals(0, find.count())
    }
}