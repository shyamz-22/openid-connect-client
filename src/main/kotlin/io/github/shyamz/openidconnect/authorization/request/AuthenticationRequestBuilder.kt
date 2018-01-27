package io.github.shyamz.openidconnect.authorization.request

import io.github.shyamz.openidconnect.configuration.model.Display
import io.github.shyamz.openidconnect.configuration.model.Prompt
import io.github.shyamz.openidconnect.configuration.ClientConfiguration
import io.github.shyamz.openidconnect.configuration.model.ResponseType
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import org.apache.http.client.utils.URIBuilder

class AuthenticationRequestBuilder {

    private val authenticationRequestParams: MutableMap<String, String> = mutableMapOf()

    fun basic(): AuthenticationRequestBuilder {
        authenticationRequestParams["response_type"] = ResponseType.Code.parameter
        return this
    }

    fun scope(scopes: Set<String>): AuthenticationRequestBuilder {
        authenticationRequestParams["scope"] = scopes.joinToString(" ")
        return this
    }

    fun state(stateGenerator: () -> String): AuthenticationRequestBuilder {
        authenticationRequestParams["state"] = stateGenerator.invoke()
        return this
    }

    fun nonce(nonceGenerator: () -> String): AuthenticationRequestBuilder {
        authenticationRequestParams["nonce"] = nonceGenerator.invoke()
        return this
    }

    fun responseMode(responseMode: String): AuthenticationRequestBuilder {
        authenticationRequestParams["response_mode"] = responseMode
        return this
    }

    fun display(display: Display): AuthenticationRequestBuilder {
        authenticationRequestParams["display"] = display.actualValue()
        return this
    }

    fun prompt(prompts: Set<Prompt>): AuthenticationRequestBuilder {
        authenticationRequestParams["prompt"] = prompts.validate()
                .joinToString(" ") { it.actualValue() }

        return this
    }

    fun maxAge(allowableElapsedTimeInSeconds: Long): AuthenticationRequestBuilder {
        authenticationRequestParams["max_age"] = allowableElapsedTimeInSeconds.toString()
        return this
    }

    fun uiLocales(locales: Set<String>): AuthenticationRequestBuilder {
        authenticationRequestParams["ui_locales"] = locales.joinToString(" ")
        return this
    }

    fun idTokenHint(idTokenValue: String): AuthenticationRequestBuilder {
        authenticationRequestParams["id_token_hint"] = idTokenValue
        return this
    }

    fun loginHint(loginHintValue: String): AuthenticationRequestBuilder {
        authenticationRequestParams["login_hint"] = loginHintValue
        return this
    }

    fun authenticationContextClassReference(acrValues: Set<String>): AuthenticationRequestBuilder {
        authenticationRequestParams["acr_values"] = acrValues.joinToString(" ")
        return this
    }

    fun overrideRedirectUri(redirectUri: String): AuthenticationRequestBuilder {
        authenticationRequestParams["redirect_uri"] = redirectUri
        return this
    }

    fun build(): AuthorizationRequest {

        authenticationRequestParams["response_type"] ?: throw OpenIdConnectException("Please choose a flow parameter")
        authenticationRequestParams["scope"] ?: authenticationRequestParams.put("scope", "openid")
        authenticationRequestParams["redirect_uri"] ?: authenticationRequestParams.put("redirect_uri", ClientConfiguration.client.redirectUri)

        val authorizeUrl = URIBuilder(ClientConfiguration.provider.authorizationEndpoint)
                .addParameter("client_id", ClientConfiguration.client.id)
                .apply {
                    authenticationRequestParams.forEach {
                        this.addParameter(it.key, it.value)
                    }
                }
                .build()
                .toString()

        return AuthorizationRequest(authorizeUrl)
    }

    private fun Set<Prompt>.validate(): Set<Prompt> {
        if (contains(Prompt.None) && size > 1)
            throw OpenIdConnectException("prompt 'none' cannot be provided with any other value")

        return this
    }
}