package io.github.shyamz.openidconnect.authorization.request

import io.github.shyamz.openidconnect.configuration.model.GrantType
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import io.github.shyamz.openidconnect.response.model.BasicFlowResponse
import io.github.shyamz.openidconnect.response.model.Grant
import io.github.shyamz.openidconnect.token.TokenService

data class AuthorizationCodeGrant(val code: String) : Grant(GrantType.AuthorizationCode) {
    init {
        with(code) {
            if (this.isBlank()) {
                throw OpenIdConnectException("IdP returned empty or blank {\"authorization code\": \"$code\"}")
            }
        }
    }

    fun exchangeCodeForTokens(): BasicFlowResponse {
        return TokenService().exchange(this)
    }
}


