package io.github.shyamz.openidconnect.exceptions

import io.github.shyamz.openidconnect.provider.model.ErrorResponse

class OpenIdConnectException(message: String,
                             val errorResponse: ErrorResponse? = null) : RuntimeException(message)