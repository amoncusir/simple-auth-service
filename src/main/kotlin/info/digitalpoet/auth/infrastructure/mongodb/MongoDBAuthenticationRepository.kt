package info.digitalpoet.auth.infrastructure.mongodb

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import info.digitalpoet.auth.domain.model.Authentication
import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.repository.InvalidAuthentication
import info.digitalpoet.auth.domain.values.RefreshId
import info.digitalpoet.auth.domain.values.UserId
import org.litote.kmongo.getCollection

class MongoDBAuthenticationRepository(database: MongoDatabase): AuthenticationRepository
{
    private val collection: MongoCollection<UserPersistence> by lazy {
        database.getCollection<UserPersistence>("tokens")
    }

    override fun save(authentication: Authentication): Authentication {
        if (authentication.refreshId == null)
            throw InvalidAuthentication("Only can save authentication with refreshId!")

        TODO("Not yet implemented")
    }

    override fun delete(refreshId: RefreshId): Authentication {
        TODO("Not yet implemented")
    }

    override fun deleteByUserId(userId: UserId): List<Authentication> {
        TODO("Not yet implemented")
    }

    override fun findByUserId(userId: UserId): List<Authentication> {
        TODO("Not yet implemented")
    }
}