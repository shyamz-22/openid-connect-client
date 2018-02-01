package io.github.shyamz.openidconnect.response.model

import io.github.shyamz.openidconnect.validation.JwtToken
import java.util.*

data class BasicFlowResponse(val tokenType: String,
                             val expiresAt: Date?,
                             val accessToken: String,
                             val idToken: String,
                             val refreshToken: String?) {

    fun extractAuthenticatedUserInfo(nonce: String? = null): AuthenticatedUser {
        val claims = JwtToken(idToken, nonce).claims()
        val clientInfo = if (claims.audience.size == 1)
            ClientInfo(claims.audience.first())
        else {
            val authorizedClient = claims.getStringClaim("azp")
            val interestedClient = claims.audience.filterNot { it == authorizedClient }
            ClientInfo(authorizedClient, interestedClient)
        }

        val userInfo = UserInfo(Profile(claims.subject))

        return AuthenticatedUser(this, clientInfo, userInfo)
    }
}