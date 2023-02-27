package info.digitalpoet.auth.infrastrucutre.mongodb

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import com.typesafe.config.ConfigFactory
import info.digitalpoet.auth.ApplicationEngineTest
import info.digitalpoet.auth.createTestApplicationWithConfig
import info.digitalpoet.auth.domain.model.Policies
import info.digitalpoet.auth.domain.model.Policy
import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.repository.DuplicateEntity
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.domain.values.UserId
import info.digitalpoet.auth.extension.GenericStaticContainerExtension
import info.digitalpoet.auth.extension.WithMongoDBContainer
import info.digitalpoet.auth.infrastructure.mongodb.MongoDBUserRepository
import info.digitalpoet.auth.koin
import info.digitalpoet.auth.module.eventsModule
import info.digitalpoet.auth.module.infrastructureModule
import info.digitalpoet.auth.module.repositoryModule
import io.ktor.server.config.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@WithMongoDBContainer
@GenericStaticContainerExtension.Lifecycle(GenericStaticContainerExtension.Lifecycle.CleanUp.AFTER_METHOD)
class MongoDBUserRepositoryTest: ApplicationEngineTest()
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

    @Test
    fun `save user`()
    {
        val repository = getRepo<UserRepository>()
        val database = get<MongoDatabase>()

        val user = User(
            UserId.new(),
            Email("test@test.test"),
            "hashhh",
            true,
            Policies(Policy("auth", "self"))
        )

        repository.save(user)

        val collection = database.getCollection(MongoDBUserRepository.COLLECTION)
        val doc = collection.find(eq("_id", user.userId.toString()))
            .first()

        assertNotNull(doc)
        assertEquals(doc["email"], user.email.toString())
        assertEquals(doc["isActive"], user.isActive)
    }

    @Test
    fun `save user twice must be throw error on second save`()
    {
        val repository = getRepo<UserRepository>()
        val database = get<MongoDatabase>()

        val user = User(
            UserId.new(),
            Email("test@test.test"),
            "hashhh",
            true,
            Policies(Policy("auth", "self"))
        )

        repository.save(user)

        val collection = database.getCollection(MongoDBUserRepository.COLLECTION)
        val doc = collection.find(eq("_id", user.userId.toString()))
            .first()

        assertNotNull(doc)
        assertEquals(doc["email"], user.email.toString())
        assertEquals(doc["isActive"], user.isActive)

        assertThrows<DuplicateEntity> {
            repository.save(user)
        }
    }

    @Test
    fun `save user and get by Id`()
    {
        val repository = getRepo<UserRepository>()

        val user = User(
            UserId.new(),
            Email("test@test.test"),
            "hashhh",
            true,
            Policies(Policy("auth", "self"))
        )

        repository.save(user)
        val savedUser = repository.findById(user.userId)

        assertNotNull(savedUser)
        assertEquals(user.userId, savedUser.userId)
        assertEquals(user.email, savedUser.email)
        assertEquals(user.hashedPassword, savedUser.hashedPassword)
        assertEquals(user.isActive, savedUser.isActive)
        assertEquals(user.policies, savedUser.policies)
    }

    @Test
    fun `save user and get by Email`()
    {
        val repository = getRepo<UserRepository>()

        val user = User(
            UserId.new(),
            Email("test@test.test"),
            "hashhh",
            true,
            Policies(Policy("auth", "self"))
        )

        repository.save(user)
        val savedUser = repository.findUserByEmail(user.email)

        assertNotNull(savedUser)
        assertEquals(user.userId, savedUser.userId)
        assertEquals(user.email, savedUser.email)
        assertEquals(user.hashedPassword, savedUser.hashedPassword)
        assertEquals(user.isActive, savedUser.isActive)
        assertEquals(user.policies, savedUser.policies)
    }
}