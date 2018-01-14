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


        assertThatThrownBy {
            AuthenticationRequestBuilder(
                    googleProviderConfig,
                    OpenIdClient(CLIENT_ID, CLIENT_REDIRECT_URI))
                    .build()
        }
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

    @Test
    fun `build - can build a basic flow authentication request with prompts`() {

        val googleProviderConfig = WellKnownConfigDiscoverer(URI.create("https://accounts.google.com/"))
                .identityProviderConfiguration()

        val authenticationRequest = AuthenticationRequestBuilder(googleProviderConfig,
                OpenIdClient(CLIENT_ID, CLIENT_REDIRECT_URI))
                .basic()
                .prompt(setOf(Prompt.SelectAccount, Prompt.Consent))
                .build()

        assertThat(authenticationRequest).isEqualTo(expectedUrlWithPrompt("select_account+consent"))
    }

    @Test
    fun `build - throws Exception when more than one prompt is presented for 'none' prompt`() {

        val googleProviderConfig = WellKnownConfigDiscoverer(URI.create("https://accounts.google.com/"))
                .identityProviderConfiguration()


        assertThatThrownBy {
            AuthenticationRequestBuilder(
                    googleProviderConfig,
                    OpenIdClient(CLIENT_ID, CLIENT_REDIRECT_URI))
                    .basic()
                    .prompt(setOf(Prompt.None, Prompt.Login))
                    .build()
        }
                .isInstanceOf(OpenIdConnectException::class.java)
                .hasFieldOrPropertyWithValue("message", "prompt 'none' cannot be provided with anyother value")
    }


    @Test
    fun `build - can build a basic flow authentication request with optional parameters`() {

        val googleProviderConfig = WellKnownConfigDiscoverer(URI.create("https://accounts.google.com/"))
                .identityProviderConfiguration()

        val authenticationRequest = AuthenticationRequestBuilder(googleProviderConfig,
                OpenIdClient(CLIENT_ID, CLIENT_REDIRECT_URI))
                .basic()
                .responseMode("query")
                .nonce({ NONCE_VALUE })
                .display(Display.Page)
                .maxAge(3600)
                .uiLocales(setOf("fr-CA", "fr", "en"))
                .idTokenHint(ID_TOKEN_VALUE)
                .loginHint("abc@gmail.com")
                .authenticationContextClassReference(setOf("loa-1", "loa-2"))
                .build()

        assertThat(authenticationRequest).isEqualTo(expectedUrlWith(
                responseMode = "query",
                nonce = NONCE_VALUE,
                display = "page",
                maxAge = "3600",
                uiLocales = "fr-CA+fr+en",
                idTokenHint = ID_TOKEN_VALUE,
                loginHint = "abc%40gmail.com",
                authenticationContextClassReference = "loa-1+loa-2"))
    }

    private fun expectedUrlWithState(): String {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=$CLIENT_ID" +
                "&redirect_uri=$ENCODED_CLIENT_REDIRECT_URL" +
                "&scope=openid" +
                "&response_type=code" +
                "&state=randomState"
    }

    private fun expectedUrlWithOutState(scopes: String = "openid"): String {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=$CLIENT_ID" +
                "&redirect_uri=$ENCODED_CLIENT_REDIRECT_URL" +
                "&scope=$scopes" +
                "&response_type=code"
    }

    private fun expectedUrlWithPrompt(prompt: String): String {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=$CLIENT_ID" +
                "&redirect_uri=$ENCODED_CLIENT_REDIRECT_URL" +
                "&scope=openid" +
                "&response_type=code" +
                "&prompt=$prompt"
    }

    private fun expectedUrlWith(responseMode: String,
                                nonce: String,
                                display: String,
                                maxAge: String,
                                uiLocales: String,
                                idTokenHint: String,
                                loginHint: String, authenticationContextClassReference: String): String {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=$CLIENT_ID" +
                "&redirect_uri=$ENCODED_CLIENT_REDIRECT_URL" +
                "&scope=openid" +
                "&response_type=code" +
                "&response_mode=$responseMode" +
                "&nonce=$nonce" +
                "&display=$display" +
                "&max_age=$maxAge" +
                "&ui_locales=$uiLocales" +
                "&id_token_hint=$idTokenHint" +
                "&login_hint=$loginHint" +
                "&acr_values=$authenticationContextClassReference"

    }

    companion object {
        private val CLIENT_REDIRECT_URI = "https://openidconnect.net/callback"
        private val ENCODED_CLIENT_REDIRECT_URL = "https%3A%2F%2Fopenidconnect.net%2Fcallback"
        private val CLIENT_ID = "client-id"
        private val CLIENT_STATE_VALUE = "randomState"
        private val NONCE_VALUE = "aVeryRandomValue"
        private val ID_TOKEN_VALUE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ"
    }
}

