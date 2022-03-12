package info.digitalpoet.auth.domain.model

enum class PolicyEffect {
    ALLOW,
    DENY
}

data class Policy(
    val service: String,
    val actions: List<String>,
    val effect: PolicyEffect
)
