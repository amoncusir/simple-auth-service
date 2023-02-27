package info.digitalpoet.auth.infrastructure.mongodb

import com.mongodb.MongoWriteException
import com.mongodb.client.MongoDatabase
import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.repository.DuplicateEntity
import info.digitalpoet.auth.domain.repository.NotFoundEntity
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.domain.values.UserId
import info.digitalpoet.auth.infrastructure.mongodb.data.UserPersistence
import info.digitalpoet.auth.infrastructure.mongodb.data.toPersistence
import org.litote.kmongo.*

class MongoDBUserRepository(
    database: MongoDatabase
): UserRepository
{
    companion object {
        const val COLLECTION = "user"
    }

    private val collection by lazy {
        database.getCollection<UserPersistence>(COLLECTION)
    }

    override fun save(entity: User): User
    {
        val persistenceUser = entity.toPersistence()

        try {
            collection.insertOne(persistenceUser)
        } catch (e: MongoWriteException) {
            throw when(e.error.code) {
                11000 -> DuplicateEntity("Duplicate user entity for id: ${entity.userId}", e)
                else -> e
            }
        }

        return entity.copy()
    }

    override fun update(entity: User): User
    {
        val persistenceUser = entity.toPersistence()

        try {
            collection.updateOne(persistenceUser)
        } catch (e: MongoWriteException) {
            throw e
        }

        return entity.copy()
    }

    override fun findById(id: UserId): User
    {
        val result = collection.findOneById(id.toPersistence()) ?: throw NotFoundEntity(id.toString(), "User ID")

        return result.toDomain()
    }

    override fun findUserByEmail(email: Email): User
    {
        val result = collection.findOne(UserPersistence::email eq email.toString())
            ?: throw NotFoundEntity(email.toString(), "User Email")

        return result.toDomain()
    }
}