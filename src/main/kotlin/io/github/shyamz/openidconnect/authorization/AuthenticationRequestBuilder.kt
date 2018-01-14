package io.github.shyamz.openidconnect.authorization

import io.github.shyamz.openidconnect.configuration.IdProviderConfiguration
import io.github.shyamz.openidconnect.configuration.model.ResponseType
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import org.apache.http.client.utils.URIBuilder

class AuthenticationRequestBuilder(private val idProviderConfiguration: IdProviderConfiguration,
                                   private val client: OpenIdClient) {

    private val authenticationRequestParams: MutableMap<String, String> = mutableMapOf()

    fun basic(): AuthenticationRequestBuilder {
        authenticationRequestParams["response_type"] = ResponseType.Code.type
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
                                                       .joinToString(" ")  { it.actualValue() }

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

    fun build(): String {

        authenticationRequestParams["response_type"] ?: throw OpenIdConnectException("Please choose a flow type")
        authenticationRequestParams["scope"] ?: authenticationRequestParams.put("scope", "openid")

        return URIBuilder(idProviderConfiguration.authorizationEndpoint)
                .addParameter("client_id", client.id)
                .addParameter("redirect_uri", client.redirectUri)
                .apply {
                    authenticationRequestParams.forEach {
                        this.addParameter(it.key, it.value)
                    }
                }
                .build()
                .toString()
    }

    private fun Set<Prompt>.validate(): Set<Prompt> {
        if (contains(Prompt.None) && size > 1)
            throw OpenIdConnectException("prompt 'none' cannot be provided with any other value")

        return this
    }

}

class OpenIdClient(val id: String,
                   val redirectUri: String)

enum class Display {
    Page,
    Popup,
    Touch,
    Wap;

    fun actualValue(): String {
        return this.name.toLowerCase()
    }
}

enum class Prompt {
    None,
    Login,
    Consent,
    SelectAccount;

    fun actualValue(): String {
        return when (SelectAccount) {
            this -> "select_account"
            else -> this.name.toLowerCase()
        }
    }

}