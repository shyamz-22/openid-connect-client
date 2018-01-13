package io.github.shyamz.openidconnect.configuration.model

enum class CodeChallengeMethod(val method: String) {
    Plain("plain"),
    S256("S256"),
    RS256("RS256"),
    ES256("ES256")
}