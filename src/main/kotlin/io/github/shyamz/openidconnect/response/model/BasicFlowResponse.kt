package io.github.shyamz.openidconnect.response.model

data class BasicFlowResponse(val tokenType: String,
                             val expiresIn: Int?,
                             val accessToken: String,
                             val idToken: String,
                             val refreshToken: String?)