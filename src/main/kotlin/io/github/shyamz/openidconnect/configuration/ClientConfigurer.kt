package io.github.shyamz.openidconnect.configuration

import io.github.shyamz.openidconnect.authorization.request.OpenIdClient
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod
import io.github.shyamz.openidconnect.discovery.WellKnownConfigDiscoverer
import java.net.URI

class ClientConfigurer {

    private lateinit var issuer: String
    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private lateinit var redirectUri: String
    private lateinit var authMethod: TokenEndPointAuthMethod

    fun issuer(issuerUrl: String): ClientConfigurer {
        this.issuer = issuerUrl
        return this
    }

    fun clientId(clientId: String): ClientConfigurer {
        this.clientId = clientId
        return this
    }

    fun clientSecret(clientSecret: String): ClientConfigurer {
        this.clientSecret = clientSecret
        return this
    }

    fun redirectUri(redirectUri: String): ClientConfigurer {
        this.redirectUri = redirectUri
        return this
    }

    fun tokenEndPointAuthMethod(authenticationMethod: TokenEndPointAuthMethod): ClientConfigurer {
        this.authMethod = authenticationMethod
        return this
    }

    fun load(): ClientConfiguration {
        return ClientConfiguration.apply {
            provider = WellKnownConfigDiscoverer(URI.create(issuer)).identityProviderConfiguration()
            client = OpenIdClient(clientId, redirectUri, clientSecret)
            tokenEndPointAuthMethod = authMethod
        }
    }

}

object ClientConfiguration {

    internal lateinit var provider: IdProviderConfiguration
    internal lateinit var client: OpenIdClient
    internal lateinit var tokenEndPointAuthMethod: TokenEndPointAuthMethod

    fun with(): ClientConfigurer {
        return ClientConfigurer()
    }
}
