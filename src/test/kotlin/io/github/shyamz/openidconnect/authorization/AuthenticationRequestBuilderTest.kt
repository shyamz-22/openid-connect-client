package io.github.shyamz.openidconnect.authorization

import io.github.shyamz.openidconnect.TestConstants.CLIENT_ID
import io.github.shyamz.openidconnect.TestConstants.CLIENT_REDIRECT_URI
import io.github.shyamz.openidconnect.TestConstants.CLIENT_STATE_VALUE
import io.github.shyamz.openidconnect.TestConstants.ID_TOKEN_VALUE
import io.github.shyamz.openidconnect.TestConstants.NONCE_VALUE
import io.github.shyamz.openidconnect.discovery.WellKnownConfigDiscoverer
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import org.assertj.core.api.AbstractUriAssert
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

        authenticationRequestAssert(authenticationRequest)
                .hasParameter("state", CLIENT_STATE_VALUE)

    }

    @Test
    fun `build - can build a basic flow authentication request without state parameter`() {

        val googleProviderConfig = WellKnownConfigDiscoverer(URI.create("https://accounts.google.com/"))
                .identityProviderConfiguration()

        val authenticationRequest = AuthenticationRequestBuilder(googleProviderConfig,
                OpenIdClient(CLIENT_ID, CLIENT_REDIRECT_URI))
                .basic()
                .build()

        authenticationRequestAssert(authenticationRequest)
                .hasNoParameter("state")
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
                .hasFieldOrPropertyWithValue("message", "Please choose a flow parameter")
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

        authenticationRequestAssert(authenticationRequest, "openid email profile")
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

        authenticationRequestAssert(authenticationRequest)
                .hasParameter("prompt", "select_account consent")
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
                .hasFieldOrPropertyWithValue("message", "prompt 'none' cannot be provided with any other value")
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

        authenticationRequestAssert(authenticationRequest)
                .hasParameter("response_mode", "query")
                .hasParameter("nonce", NONCE_VALUE)
                .hasParameter("display", "page")
                .hasParameter("max_age", "3600")
                .hasParameter("ui_locales", "fr-CA fr en")
                .hasParameter("id_token_hint", ID_TOKEN_VALUE)
                .hasParameter("login_hint", "abc@gmail.com")
                .hasParameter("acr_values", "loa-1 loa-2")
    }

    private fun authenticationRequestAssert(authenticationRequest: AuthorizationRequest,
                                            scopes: String = "openid"): AbstractUriAssert<*> {
        return assertThat(URI.create(authenticationRequest.authorizeUrl))
                .hasHost("accounts.google.com")
                .hasPath("/o/oauth2/v2/auth")
                .hasScheme("https")
                .hasParameter("client_id", CLIENT_ID)
                .hasParameter("redirect_uri", CLIENT_REDIRECT_URI)
                .hasParameter("response_type", "code")
                .hasParameter("scope", scopes)
    }
}
