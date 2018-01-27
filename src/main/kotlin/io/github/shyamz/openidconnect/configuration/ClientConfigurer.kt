package io.github.shyamz.openidconnect.configuration

import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod
import io.github.shyamz.openidconnect.discovery.WellKnownConfigDiscoverer
import java.net.URI

class ClientConfigurer {

    fun issuer(issuerUrl: String): ClientConfigurer {
        ClientConfiguration.provider =  WellKnownConfigDiscoverer(URI.create(issuerUrl)).identityProviderConfiguration()
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
}

object ClientConfiguration {

    internal lateinit var provider: IdProviderConfiguration
    internal lateinit var client: OpenIdClient
    internal lateinit var tokenEndPointAuthMethod: TokenEndPointAuthMethod

    fun with(): ClientConfigurer {
        return ClientConfigurer()
    }
}
