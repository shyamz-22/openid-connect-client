package io.github.shyamz.openidconnect.discovery

import io.github.shyamz.openidconnect.UnirestFactory
import io.github.shyamz.openidconnect.configuration.IdProviderConfiguration
import java.net.URI

class WellKnownConfigDiscoverer(private val issuer: URI) {

    companion object {
        private const val WELLKNOWN_PATH = "/.well-known/openid-configuration"
    }

    fun identityProviderConfiguration(): IdProviderConfiguration {
        return UnirestFactory().get(issuer.toString().plus(WELLKNOWN_PATH))
                .asObject(ProviderConfigurationModel::class.java)
                .body.idProviderConfig()
    }
}