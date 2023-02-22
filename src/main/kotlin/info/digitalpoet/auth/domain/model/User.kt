package info.digitalpoet.auth.domain.model

import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.domain.values.UserId

data class User(
    val userId: UserId,
    val email: Email,
    val hashedPassword: String,
    val isActive: Boolean,
    val policies: List<Policy>
) {
    fun isValid(): Boolean = this.isActive
}
