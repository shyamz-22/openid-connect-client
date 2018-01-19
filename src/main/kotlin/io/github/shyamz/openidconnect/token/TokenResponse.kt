package io.github.shyamz.openidconnect.token

import com.fasterxml.jackson.annotation.JsonProperty

internal data class TokenResponse(@JsonProperty("token_type") val tokenType: String,
                                  @JsonProperty("expires_in") val expiresIn: Int,
                                  @JsonProperty("access_token") val accessToken: String?,
                                  @JsonProperty("id_token") val idToken: String?,
                                  @JsonProperty("refresh_token") val refreshToken: String?) {

    fun toBasicFlowResponse(): BasicFlowResponse {
        return BasicFlowResponse(tokenType, expiresIn, accessToken!!, idToken!!, refreshToken!!)
    }
}


data class BasicFlowResponse(val tokenType: String,
                             val expiresIn: Int,
                             val accessToken: String,
                             val idToken: String,
                             val refreshToken: String)
