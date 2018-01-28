package io.github.shyamz.openidconnect.response.model

import io.github.shyamz.openidconnect.validation.JwtToken

data class BasicFlowResponse(val tokenType: String,
                             val expiresIn: Int?,
                             val accessToken: String,
                             val idToken: String,
                             val refreshToken: String?) {

    fun authenticatedUser(nonce: String? = null): AuthenticatedUser {
        return AuthenticatedUser(this, JwtToken(idToken, nonce).claims())
    }
}