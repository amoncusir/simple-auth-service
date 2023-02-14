package info.digitalpoet.auth.domain.repository.dynamodb

import info.digitalpoet.auth.domain.model.Authentication
import info.digitalpoet.auth.domain.repository.AuthenticationRepository

class DynamoAuthenticationRepository: AuthenticationRepository {

    override fun save(authentication: Authentication): Authentication {
        TODO("Not yet implemented")
    }

    override fun delete(refreshId: String): Authentication {
        TODO("Not yet implemented")
    }

    override fun deleteByUserId(userId: String): List<Authentication> {
        TODO("Not yet implemented")
    }

    override fun findByUserId(userId: String): List<Authentication> {
        TODO("Not yet implemented")
    }
}