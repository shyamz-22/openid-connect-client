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

    //optional params
    private var responseMode: String? = null
    private var nonce: String? = null
    private var display: Display? = null
    private var prompts: Set<Prompt>? = null
    private var maxAge: Long? = null
    private var locales: Set<String>? = null
    private var idTokenHint: String? = null
    private var loginHint: String? = null
    private var acrValues: Set<String>? = null


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

    fun nonce(nonceGenerator: () -> String): AuthenticationRequestBuilder {
        this.nonce = nonceGenerator.invoke()
        return this
    }

    fun responseMode(responseMode: String): AuthenticationRequestBuilder {
        this.responseMode = responseMode
        return this
    }

    fun display(display: Display): AuthenticationRequestBuilder {
        this.display = display
        return this
    }

    fun prompt(prompts: Set<Prompt>): AuthenticationRequestBuilder {
        if (containsNoneWithAnyOtherPrompt(prompts)) {
            throw OpenIdConnectException("prompt 'none' cannot be provided with anyother value")
        }

        this.prompts = prompts
        return this
    }

    private fun containsNoneWithAnyOtherPrompt(prompts: Set<Prompt>) =
            prompts.contains(Prompt.None) && prompts.size > 1

    fun maxAge(allowableElapsedTimeInSeconds: Long): AuthenticationRequestBuilder {
        this.maxAge = allowableElapsedTimeInSeconds
        return this
    }

    fun uiLocales(locales: Set<String>): AuthenticationRequestBuilder {
        this.locales = locales
        return this
    }

    fun idTokenHint(idTokenValue: String): AuthenticationRequestBuilder {
        this.idTokenHint = idTokenValue
        return this
    }

    fun loginHint(loginHintValue: String): AuthenticationRequestBuilder {
        this.loginHint= loginHintValue
        return this
    }

    fun authenticationContextClassReference(acrValues: Set<String>): AuthenticationRequestBuilder {
        this.acrValues = acrValues
        return this
    }

    fun build(): String {

        val responseTypeValue = responseType?.type ?: throw OpenIdConnectException("Please choose a flow type")
        val displayValue = display?.actualValue()
        val prompt = prompts?.joinToString(" ") { it.actualValue() }
        val uiLocales = locales?.joinToString(" ")
        val acr = acrValues?.joinToString(" ")

        val builderWithMandatoryParams = URIBuilder(idProviderConfiguration.authorizationEndpoint)
                .addParameter("client_id", client.id)
                .addParameter("redirect_uri", client.redirectUri)
                .addParameter("scope", scope.joinToString(" "))
                .addParameter("response_type", responseTypeValue)

        return builderWithMandatoryParams
                .addOptionalParam("state", state)
                .addOptionalParam("response_mode", responseMode)
                .addOptionalParam("nonce", nonce)
                .addOptionalParam("display", displayValue)
                .addOptionalParam("prompt", prompt)
                .addOptionalParam("max_age", maxAge?.toString())
                .addOptionalParam("ui_locales", uiLocales)
                .addOptionalParam("id_token_hint", idTokenHint)
                .addOptionalParam("login_hint", loginHint)
                .addOptionalParam("acr_values", acr)
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

enum class Display {
    Page,
    Popup,
    Touch,
    Wap;

    fun actualValue(): String {
        return this.name.toLowerCase()
    }
}

enum class Prompt() {
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