package io.github.shyamz.openidconnect.configuration.model

/**
 * Other methods client_secret_jwt, and private_key_jwt are unsupported by the library
 */
enum class TokenEndPointAuthMethod(val supportedMethod: String) {
    Post("client_secret_post"),
    Basic("client_secret_basic"),
    None("none")
}