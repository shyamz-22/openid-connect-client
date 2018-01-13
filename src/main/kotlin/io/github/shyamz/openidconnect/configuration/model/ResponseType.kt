package io.github.shyamz.openidconnect.configuration.model

enum class ResponseType(val type: String) {
    Code("code"),
    Token("token"),
    IdToken("id_token"),
    CodeToken("code token"),
    CodeIdToken("code id_token"),
    TokenIdToken("token id_token"),
    CodeTokenIdToken("code token id_token"),
    None("none")
}