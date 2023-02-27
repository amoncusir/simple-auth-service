package info.digitalpoet.auth.infrastructure.mongodb.data

import info.digitalpoet.auth.domain.model.AuthenticationScope
import info.digitalpoet.auth.domain.model.Policies
import info.digitalpoet.auth.domain.model.Policy
import kotlinx.serialization.Serializable

@Serializable
data class PolicyServiceWithActionsPersistence(
    val service: String,
    val actions: Set<String>
) {
    constructor(from: Policy): this(
        from.service,
        from.actions
    )

    constructor(from: AuthenticationScope): this(
        from.service,
        from.grant
    )

    fun toPolicy(): Policy = Policy(service, actions)

    fun toScope(): AuthenticationScope = AuthenticationScope(service, actions)
}

fun Policy.toPersistence() = PolicyServiceWithActionsPersistence(this)
fun Policies.toPersistence() = policies.map { it.toPersistence() }

fun AuthenticationScope.toPersistence() = PolicyServiceWithActionsPersistence(this)