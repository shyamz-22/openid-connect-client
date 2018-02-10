package io.github.shyamz.openidconnect.configuration.model

enum class Display {
    Page,
    Popup,
    Touch,
    Wap;

    fun actualValue(): String {
        return this.name.toLowerCase()
    }
}