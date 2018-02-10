package io.github.shyamz.openidconnect.response.model

import io.github.shyamz.openidconnect.validation.JwtToken
import io.github.shyamz.openidconnect.validation.clientInfo
import io.github.shyamz.openidconnect.validation.userInfo
import java.util.*

data class BasicFlowResponse(val tokenType: String,
                             val expiresAt: Date?,
                             val accessToken: String,
                             val idToken: String,
                             val refreshToken: String?) {

    fun extractAuthenticatedUserInfo(nonce: String? = null): AuthenticatedUser {
        val claims = JwtToken(idToken, nonce).claims()
        return AuthenticatedUser(this, claims.clientInfo(), claims.userInfo())
    }
}