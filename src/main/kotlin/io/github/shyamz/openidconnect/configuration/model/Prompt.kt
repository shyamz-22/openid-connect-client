package io.github.shyamz.openidconnect.configuration.model

enum class Prompt {
    None,
    Login,
    Consent,
    SelectAccount;

    fun actualValue(): String {
        return when (SelectAccount) {
            this -> "select_account"
            else -> this.name.toLowerCase()
        }
    }
}