package io.github.shyamz.openidconnect.exceptions

import io.github.shyamz.openidconnect.authorization.response.model.ErrorResponse

class OpenIdConnectException(message: String,
                             val errorResponse: ErrorResponse? = null) : RuntimeException(message)