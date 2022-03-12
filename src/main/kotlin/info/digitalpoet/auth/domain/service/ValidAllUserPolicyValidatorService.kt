package info.digitalpoet.auth.domain.service

import info.digitalpoet.auth.domain.model.AuthenticationScope
import info.digitalpoet.auth.domain.model.User

class ValidAllUserPolicyValidatorService : UserPolicyValidatorService
{
    override fun validate(user: User, requestedScope: List<AuthenticationScope>) { }
}
