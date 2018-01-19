package io.github.shyamz.openidconnect.discovery

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mashape.unirest.http.ObjectMapper
import com.mashape.unirest.http.Unirest
import io.github.shyamz.openidconnect.authorization.AuthenticationRequestBuilder
import io.github.shyamz.openidconnect.authorization.OpenIdClient
import io.github.shyamz.openidconnect.configuration.IdProviderConfiguration
import java.io.IOException
import java.net.URI


class WellKnownConfigDiscoverer(private val issuer: URI) {

    init {
        Unirest.setObjectMapper(object : ObjectMapper {
            private val objectMapper = jacksonObjectMapper()

            override fun <T> readValue(value: String, valueType: Class<T>): T {
                try {
                    return objectMapper.readValue(value, valueType)
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }

            }

            override fun writeValue(value: Any): String {
                try {
                    return objectMapper.writeValueAsString(value)
                } catch (e: JsonProcessingException) {
                    throw RuntimeException(e)
                }

            }
        })
    }

    companion object {
        private const val WELLKNOWN_PATH = "/.well-known/openid-configuration"
    }

    fun identityProviderConfiguration(): IdProviderConfiguration {
        return Unirest.get(issuer.toString().plus(WELLKNOWN_PATH))
                .asObject(ProviderConfigurationModel::class.java)
                .body.idProviderConfig()
    }

    fun authorizeRequest(openIdClient: OpenIdClient): AuthenticationRequestBuilder {
        return AuthenticationRequestBuilder(identityProviderConfiguration(), openIdClient)
    }
}