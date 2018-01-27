package io.github.shyamz.openidconnect.configuration

internal data class OpenIdClient(val id: String,
                        val redirectUri: String,
                        val secret: String? = null)