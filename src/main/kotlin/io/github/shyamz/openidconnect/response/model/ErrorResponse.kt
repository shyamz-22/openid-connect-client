package io.github.shyamz.openidconnect.response.model

/**
 * https://tools.ietf.org/html/rfc6749#section-4.2.1
 */
data class ErrorResponse(val error: String,
                         val errorDescription: String? = null,
                         val errorUri: String? = null)