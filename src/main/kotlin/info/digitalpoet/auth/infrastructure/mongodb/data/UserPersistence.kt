package info.digitalpoet.auth.infrastructure.mongodb.data

import info.digitalpoet.auth.domain.model.Policies
import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.domain.values.UserId
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.id.StringId

@Serializable
data class UserPersistence(
    @Contextual
    @SerialName("_id")
    val userId: Id<String>,
    val email: String,
    val hashedPassword: String,
    val isActive: Boolean,
    val policies: List<PolicyServiceWithActionsPersistence>
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
        Policies(policies.map(PolicyServiceWithActionsPersistence::toPolicy).toSet())
    )
}

fun User.toPersistence() = UserPersistence(this)
fun UserId.toPersistence() = StringId<String>(this.toString())