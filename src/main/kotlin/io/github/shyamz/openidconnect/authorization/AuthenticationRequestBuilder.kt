package io.github.shyamz.openidconnect.authorization

import io.github.shyamz.openidconnect.configuration.IdProviderConfiguration
import io.github.shyamz.openidconnect.configuration.model.ResponseType
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import org.apache.http.client.utils.URIBuilder

class AuthenticationRequestBuilder(private val idProviderConfiguration: IdProviderConfiguration,
                                   private val client: OpenIdClient) {

    private var scope: MutableSet<String> = mutableSetOf("openid")
    private var state: String? = null
    private var responseType: ResponseType? = null

    fun basic(): AuthenticationRequestBuilder {
        responseType = ResponseType.Code
        return this
    }

    fun scope(scopes: Set<String>): AuthenticationRequestBuilder {
        scope.addAll(scopes)
        return this
    }

    fun state(stateGenerator: () -> String): AuthenticationRequestBuilder {
        this.state = stateGenerator.invoke()
        return this
    }

    fun build(): String {

        val responseTypeValue = responseType?.type ?: throw OpenIdConnectException("Please choose a flow type")

        val builderWithMandatoryParams = URIBuilder(idProviderConfiguration.authorizationEndpoint)
                .addParameter("client_id", client.id)
                .addParameter("redirect_uri", client.redirectUri)
                .addParameter("scope", scope.joinToString(" "))
                .addParameter("response_type", responseTypeValue)

        return builderWithMandatoryParams
                .addOptionalParam("state", state)
                .build()
                .toString()
    }
}

private fun URIBuilder.addOptionalParam(param: String, value: String?): URIBuilder {
    value?.apply { addParameter(param, value) }
    return this
}

class OpenIdClient(val id: String,
                   val redirectUri: String)