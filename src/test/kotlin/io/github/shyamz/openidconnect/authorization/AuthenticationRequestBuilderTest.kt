package io.github.shyamz.openidconnect.authorization

import io.github.shyamz.openidconnect.discovery.WellKnownConfigDiscoverer
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import java.net.URI

class AuthenticationRequestBuilderTest {

    @Test
    fun `build - can build a basic flow authentication request with state parameter`() {

        val googleProviderConfig = WellKnownConfigDiscoverer(URI.create("https://accounts.google.com/"))
                .identityProviderConfiguration()

        val authenticationRequest = AuthenticationRequestBuilder(
                googleProviderConfig,
                OpenIdClient(CLIENT_ID, CLIENT_REDIRECT_URI))
                .basic()
                .state({ CLIENT_STATE_VALUE })
                .build()

        assertThat(authenticationRequest).isEqualTo(expectedUrlWithState())

    }

    @Test
    fun `build - can build a basic flow authentication request without state parameter`() {

        val googleProviderConfig = WellKnownConfigDiscoverer(URI.create("https://accounts.google.com/"))
                .identityProviderConfiguration()

        val authenticationRequest = AuthenticationRequestBuilder(googleProviderConfig,
                OpenIdClient(CLIENT_ID, CLIENT_REDIRECT_URI))
                .basic()
                .build()

        assertThat(authenticationRequest).isEqualTo(expectedUrlWithOutState())
    }

    @Test
    fun `build - throws Exception when no flow is chosen`() {

        val googleProviderConfig = WellKnownConfigDiscoverer(URI.create("https://accounts.google.com/"))
                .identityProviderConfiguration()


        assertThatThrownBy { AuthenticationRequestBuilder(
                googleProviderConfig,
                OpenIdClient(CLIENT_ID, CLIENT_REDIRECT_URI))
                .build() }
                .isInstanceOf(OpenIdConnectException::class.java)
                .hasFieldOrPropertyWithValue("message", "Please choose a flow type")
    }

    @Test
    fun `build - can build a basic flow authentication request with many scopes`() {

        val googleProviderConfig = WellKnownConfigDiscoverer(URI.create("https://accounts.google.com/"))
                .identityProviderConfiguration()

        val authenticationRequest = AuthenticationRequestBuilder(googleProviderConfig,
                OpenIdClient(CLIENT_ID, CLIENT_REDIRECT_URI))
                .basic()
                .scope(setOf("openid", "email", "profile"))
                .build()

        assertThat(authenticationRequest).isEqualTo(expectedUrlWithOutState("openid+email+profile"))
    }

    private fun expectedUrlWithState(): String {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=id" +
                "&redirect_uri=$ENCODED_CLIENT_REDIRECT_URL" +
                "&scope=openid" +
                "&response_type=code" +
                "&state=randomState"
    }

    private fun expectedUrlWithOutState(scopes: String = "openid"): String {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=id" +
                "&redirect_uri=$ENCODED_CLIENT_REDIRECT_URL" +
                "&scope=$scopes" +
                "&response_type=code"
    }

    companion object {
        private val CLIENT_REDIRECT_URI = "https://openidconnect.net/callback"
        private val ENCODED_CLIENT_REDIRECT_URL = "https%3A%2F%2Fopenidconnect.net%2Fcallback"
        private val CLIENT_ID = "client-id"
        private val CLIENT_STATE_VALUE = "randomState"
    }
}

