package io.github.shyamz.openidconnect.configuration.model

enum class GrantType(val grant: String) {
    AuthorizationCode("authorization_code"),
    RefreshToken("refresh_token")
}