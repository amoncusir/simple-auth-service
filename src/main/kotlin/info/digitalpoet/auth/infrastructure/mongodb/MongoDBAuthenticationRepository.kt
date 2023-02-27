package info.digitalpoet.auth.infrastructure.mongodb

import com.mongodb.MongoWriteException
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import info.digitalpoet.auth.domain.model.Authentication
import info.digitalpoet.auth.domain.repository.AuthenticationRepository
import info.digitalpoet.auth.domain.repository.DeleteFailed
import info.digitalpoet.auth.domain.repository.DuplicateEntity
import info.digitalpoet.auth.domain.repository.InvalidAuthentication
import info.digitalpoet.auth.domain.values.RefreshId
import info.digitalpoet.auth.domain.values.UserId
import info.digitalpoet.auth.infrastructure.mongodb.data.PolicyServiceWithActionsPersistence
import info.digitalpoet.auth.infrastructure.mongodb.data.UserPersistence
import info.digitalpoet.auth.infrastructure.mongodb.data.toPersistence
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.*
import org.litote.kmongo.id.StringId
import java.time.LocalDateTime

@Serializable
data class AuthenticationPersistence(
    @Contextual
    @SerialName("_id")
    val refreshId: Id<String>,
    val user: UserPersistence,
    val client: String,
    val scope: List<PolicyServiceWithActionsPersistence>,
    @Contextual
    val ttl: LocalDateTime
) {
    constructor(from: Authentication): this(
        StringId(from.refreshId.toString()),
        from.user.toPersistence(),
        from.client,
        from.scope.map { it.toPersistence() },
        from.ttl
    )

    fun toDomain(): Authentication = Authentication(
        user.toDomain(),
        scope.map { it.toScope() },
        client,
        ttl,
        RefreshId(refreshId.toString())
    )
}

fun Authentication.toPersistence() = AuthenticationPersistence(this)

class MongoDBAuthenticationRepository(database: MongoDatabase): AuthenticationRepository
{
    companion object {
        const val COLLECTION = "tokens"
    }

    private val collection: MongoCollection<AuthenticationPersistence> by lazy {
        database.getCollection<AuthenticationPersistence>(COLLECTION)
    }

    override fun save(authentication: Authentication): Authentication
    {
        if (authentication.refreshId == null)
            throw InvalidAuthentication("Only can save authentication with refreshId!")

        val persistenceAuth = authentication.toPersistence()

        try {
            collection.insertOne(persistenceAuth)
        } catch (e: MongoWriteException) {
            throw when(e.error.code) {
                11000 -> DuplicateEntity("Duplicate authentication entity for id: ${authentication.refreshId}", e)
                else -> e
            }
        }

        return authentication.copy()
    }

    override fun delete(refreshId: RefreshId): Authentication
    {
        val result = try {
            collection.findOneAndDelete(AuthenticationPersistence::refreshId eq StringId(refreshId.toString()))
        } catch (e: MongoWriteException) {
            throw e
        }

        return result.toDomain()
    }

    override fun deleteByUserId(userId: UserId): List<Authentication>
    {
        val find = findByUserId(userId)
        val delete = collection.deleteMany("""{"user._id": "$userId"}""")

        if (delete.deletedCount != find.size.toLong()) {
            throw DeleteFailed("The deleted entities is different for the find entities. " +
                    "Deleted: ${delete.deletedCount}; Find: ${find.size}")
        }

        return find
    }

    override fun findByUserId(userId: UserId): List<Authentication> {
        val find = collection.find("""{"user._id": "$userId"}""")
        return find.toList().map { it.toDomain() }
    }
}