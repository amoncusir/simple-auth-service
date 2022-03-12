package info.digitalpoet.auth.domain.model

data class User(
    val userId: String,
    val email: String,
    val hashedPassword: String,
    val isActive: Boolean,
    val policies: List<Policy>
) {
    fun isValid(): Boolean = this.isActive
}
