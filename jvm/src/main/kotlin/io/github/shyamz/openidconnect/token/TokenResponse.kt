package io.github.shyamz.openidconnect.token

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import io.github.shyamz.openidconnect.response.model.BasicFlowResponse
import io.github.shyamz.openidconnect.response.model.ErrorResponse
import java.time.Instant
import java.util.*

internal data class TokenResponse(@JsonProperty("token_type") private val tokenType: String?,
                                  @JsonProperty("expires_in") private val expiresIn: Int?,
                                  @JsonProperty("access_token") private val accessToken: String?,
                                  @JsonProperty("id_token") private val idToken: String?,
                                  @JsonProperty("refresh_token") private val refreshToken: String?,
                                  @JsonProperty("error") private val error: String?,
                                  @JsonProperty("error_description") private val errorDescription: String?,
                                  @JsonProperty("error_uri") private val errorUri: String?) {

    internal fun toBasicFlowResponse(now: Instant = Instant.now()): BasicFlowResponse {
        validateSuccessResponse()
        return BasicFlowResponse(
                tokenType ?: "Bearer",
                expiresIn?.let { Date.from(now.plusSeconds(expiresIn.toLong())) },
                accessToken ?: throw OpenIdConnectException("accessToken cannot be null"),
                idToken ?: throw OpenIdConnectException("idToken cannot be null"),
                refreshToken
        )
    }

    internal fun toErrorResponse(): ErrorResponse {
        val actualError = error ?: "invalid_request"
        return ErrorResponse(actualError, errorDescription, errorUri)
    }

    private fun validateSuccessResponse() {
        if (accessToken.isNullOrBlank().or(idToken.isNullOrBlank())) {
            throw OpenIdConnectException("Successful response does not conform to Open id connect specification: $this")
        }
    }
}
