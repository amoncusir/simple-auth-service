package info.digitalpoet.auth.domain.command.authentication

import info.digitalpoet.auth.domain.model.AuthenticationScope
import info.digitalpoet.auth.domain.model.User

interface PolicyValidator
{
    operator fun invoke(user: User, requestedScope: List<AuthenticationScope>)
}

class ValidAllPolicyValidator: PolicyValidator
{
    override fun invoke(user: User, requestedScope: List<AuthenticationScope>) { }
}
