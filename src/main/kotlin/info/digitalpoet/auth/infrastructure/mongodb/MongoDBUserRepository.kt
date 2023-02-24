package info.digitalpoet.auth.infrastructure.mongodb

import com.mongodb.client.MongoCollection
import org.litote.kmongo.*
import com.mongodb.client.MongoDatabase
import info.digitalpoet.auth.domain.model.Policies
import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.domain.values.UserId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPersistence(
    @SerialName("_id")
    val userId: Id<String>,
    val email: String,
    val hashedPassword: String,
    val isActive: Boolean,
    val policies: List<PolicyPersistence>
)

@Serializable
data class PolicyPersistence(
    val service: String,
    val actions: Set<String>
)

class MongoDBUserRepository(
    database: MongoDatabase
): UserRepository
{
    private val collection: MongoCollection<UserPersistence> by lazy { database.getCollection<UserPersistence>("user") }

    override fun save(entity: User): User {
        TODO("Not yet implemented")
    }

    override fun update(entity: User): User {
        TODO("Not yet implemented")
    }

    override fun findById(id: UserId): User {
        TODO("Not yet implemented")
    }

    override fun delete(id: UserId): User {
        TODO("Not yet implemented")
    }

    override fun findUserByEmail(email: Email): User {
        TODO("Not yet implemented")
    }
}