package io.github.shyamz.openidconnect.provider.model

import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException

data class BasicFlowResponse(val authorizationCode: String) {
    init {
        with(authorizationCode) {
            if (this.isBlank()) {
                throw OpenIdConnectException("IdP returned empty or blank {\"authorization code\": \"$authorizationCode\"}")
            }
        }
    }
}