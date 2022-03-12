package info.digitalpoet.auth.domain.service

import info.digitalpoet.auth.domain.model.AuthenticationScope
import info.digitalpoet.auth.domain.model.User

interface UserPolicyValidatorService
{
    fun validate(user: User, requestedScope: List<AuthenticationScope>)
}
