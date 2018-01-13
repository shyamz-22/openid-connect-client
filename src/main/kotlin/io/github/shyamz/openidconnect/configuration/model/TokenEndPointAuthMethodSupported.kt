package io.github.shyamz.openidconnect.configuration.model

enum class TokenEndPointAuthMethodSupported(val supportedMethod: String) {
    Post("client_secret_post"),
    Basic("client_secret_basic")
}