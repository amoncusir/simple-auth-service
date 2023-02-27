package info.digitalpoet.auth.infrastructure.mongodb

import com.mongodb.MongoWriteException
import com.mongodb.client.MongoDatabase
import info.digitalpoet.auth.domain.model.Policies
import info.digitalpoet.auth.domain.model.Policy
import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.repository.DuplicateEntity
import info.digitalpoet.auth.domain.repository.NotFoundEntity
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.domain.values.UserId
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.*
import org.litote.kmongo.id.StringId

@Serializable
data class UserPersistence(
    @Contextual
    @SerialName("_id")
    val userId: Id<String>,
    val email: String,
    val hashedPassword: String,
    val isActive: Boolean,
    val policies: List<PolicyPersistence>
) {
    constructor(from: User): this(
        from.userId.toPersistence(),
        from.email.toString(),
        from.hashedPassword,
        from.isActive,
        from.policies.toPersistence()
    )

    fun toDomain(): User = User(
        UserId(userId.toString()),
        Email(email),
        hashedPassword,
        isActive,
        Policies(policies.map(PolicyPersistence::toDomain).toSet())
    )
}

fun User.toPersistence() = UserPersistence(this)
fun UserId.toPersistence() = StringId<String>(this.toString())

@Serializable
data class PolicyPersistence(
    val service: String,
    val actions: Set<String>
) {
    constructor(from: Policy): this(
        from.service,
        from.actions
    )

    fun toDomain(): Policy = Policy(service, actions)
}

fun Policy.toPersistence() = PolicyPersistence(this)
fun Policies.toPersistence() = policies.map { it.toPersistence() }

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