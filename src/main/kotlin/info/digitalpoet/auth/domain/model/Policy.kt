package info.digitalpoet.auth.domain.model

data class Policy(
    val service: String,
    val actions: Set<String>
) {
    companion object {
        const val WILDCARD = "*"

        fun buildWildcard(service: String) = Policy(service, WILDCARD)
    }
    constructor(service: String, vararg action: String): this(service, action.toSet())
    override fun hashCode(): Int = service.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Policy

        if (service != other.service) return false

        return true
    }

    fun isAllowed(authenticationScope: AuthenticationScope): Boolean
    {
        if (service != authenticationScope.service) return false
        if (actions.contains(WILDCARD)) return true
        return actions.containsAll(authenticationScope.grant)
    }
}

data class Policies(val policies: Set<Policy>)
{
    constructor(vararg policy: Policy): this(policy.toSet())

    fun isAllowed(authenticationScopes: Collection<AuthenticationScope>): Boolean
    {
        return authenticationScopes.map { isAllowed(it) }
            .reduce { acc, b -> acc && b }
    }

    fun isAllowed(authenticationScope: AuthenticationScope): Boolean
    {
        if (policies.isEmpty()) return false
        return policies.map { it.isAllowed(authenticationScope) }
            .reduce() { acc, b -> acc && b }
    }
}
