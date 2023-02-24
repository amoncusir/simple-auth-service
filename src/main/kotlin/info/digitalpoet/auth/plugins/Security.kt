package info.digitalpoet.auth.plugins

import info.digitalpoet.auth.plugins.security.AuthenticationProfile
import info.digitalpoet.auth.plugins.security.jwtAuthentication
import info.digitalpoet.auth.plugins.security.toToken
import info.digitalpoet.auth.plugins.security.tokenValidationForGrants
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.hsts.*

fun Application.configureSecurity() {
    val jwtConfiguration = environment.config.config("jwt")

    authentication {

        val issuer = jwtConfiguration.property("issuer").getString()
        val audience = jwtConfiguration.property("audience").getString()
        val ownRealm = jwtConfiguration.property("realm").getString()

        jwtAuthentication(
            name = AuthenticationProfile.SELF.profile,
            secret = jwtConfiguration.property("secret").getString(),
            issuer = issuer,
            audience = audience,
            ownRealm = ownRealm,
            validateFun = tokenValidationForGrants(audience, "self")
        )

        jwtAuthentication(
            name = AuthenticationProfile.ADMIN.profile,
            secret = jwtConfiguration.property("secret").getString(),
            issuer = issuer,
            audience = audience,
            ownRealm = ownRealm,
            validateFun = tokenValidationForGrants(audience, "admin")
        )

        jwtAuthentication(
            name = AuthenticationProfile.SERVICE.profile,
            secret = jwtConfiguration.property("service-secret").getString(),
            issuer = issuer,
            audience = audience,
            ownRealm = ownRealm,
            validateFun = { jwtCredential ->
                if(jwtCredential.subject.isNullOrEmpty()) null else jwtCredential.toToken()
            }
        )
    }

    install(HSTS) {
        includeSubDomains = true
    }
}

