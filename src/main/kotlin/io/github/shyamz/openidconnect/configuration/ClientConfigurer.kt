package io.github.shyamz.openidconnect.configuration

import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod
import io.github.shyamz.openidconnect.discovery.WellKnownConfigDiscoverer
import java.net.URI

class ClientConfigurer {

    fun issuer(issuerUrl: String): ClientConfigurer {
        ClientConfiguration.provider = WellKnownConfigDiscoverer(URI.create(issuerUrl)).identityProviderConfiguration()
        return this
    }

    fun client(clientId: String, redirectUri: String, clientSecret: String?): ClientConfigurer {
        ClientConfiguration.client = OpenIdClient(clientId, redirectUri, clientSecret)
        return this
    }

    fun tokenEndPointAuthMethod(authenticationMethod: TokenEndPointAuthMethod): ClientConfigurer {
        ClientConfiguration.tokenEndPointAuthMethod = authenticationMethod
        return this
    }

    fun maxAgeSinceUserAuthenticated(seconds: Long): ClientConfigurer {
        ClientConfiguration.maxAgeSinceUserAuthenticated = seconds
        return this
    }

    fun clockSkewSeconds(seconds: Long): ClientConfigurer {
        ClientConfiguration.clockSkewSeconds = seconds
        return this
    }


}

object ClientConfiguration {

    internal lateinit var provider: IdProviderConfiguration
    internal lateinit var client: OpenIdClient
    internal var tokenEndPointAuthMethod: TokenEndPointAuthMethod = TokenEndPointAuthMethod.Basic
    internal var maxAgeSinceUserAuthenticated: Long = 300 // 5 minutes
    internal var clockSkewSeconds: Long = 60 // 1 minute

    fun with(): ClientConfigurer {
        return ClientConfigurer()
    }
}
