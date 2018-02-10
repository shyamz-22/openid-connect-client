package io.github.shyamz.openidconnect.exceptions

import io.github.shyamz.openidconnect.response.model.ErrorResponse

class OpenIdConnectException(message: String,
                             val errorResponse: ErrorResponse? = null) : RuntimeException(message.format(errorResponse))

private fun String.format(errorResponse: ErrorResponse?) =
        errorResponse?.let { "${this} : $it" } ?: this