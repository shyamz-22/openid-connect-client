package io.github.shyamz.openidconnect.authorization

import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.github.shyamz.openidconnect.TestConstants.CLIENT_ID
import io.github.shyamz.openidconnect.TestConstants.CLIENT_REDIRECT_URI
import io.github.shyamz.openidconnect.TestConstants.CLIENT_STATE_VALUE
import io.github.shyamz.openidconnect.TestConstants.DIFFERENT_CLIENT_REDIRECT_URI
import io.github.shyamz.openidconnect.TestConstants.ID_TOKEN_VALUE
import io.github.shyamz.openidconnect.TestConstants.NONCE_VALUE
import io.github.shyamz.openidconnect.TestConstants.loadClientConfiguration
import io.github.shyamz.openidconnect.authorization.request.AuthenticationRequestBuilder
import io.github.shyamz.openidconnect.authorization.request.AuthorizationRequest
import io.github.shyamz.openidconnect.configuration.model.Display
import io.github.shyamz.openidconnect.configuration.model.Prompt
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod.Basic
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import io.github.shyamz.openidconnect.mocks.stubForMockIdentityProvider
import org.assertj.core.api.AbstractUriAssert
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.URI

class AuthenticationRequestBuilderTest {

    @JvmField
    @Rule
    val wireMockRule = WireMockRule(8089)

    private lateinit var subject: AuthenticationRequestBuilder

    @Before
    fun setUp() {
        stubForMockIdentityProvider()
        loadClientConfiguration("http://localhost:8089", Basic)
        subject = AuthenticationRequestBuilder()
    }

    @Test
    fun `build - can build a basic flow authentication request with state parameter`() {

        val authenticationRequest = subject
                .basic()
                .state({ CLIENT_STATE_VALUE })
                .build()

        authenticationRequestAssert(authenticationRequest)
                .hasParameter("state", CLIENT_STATE_VALUE)

    }

    @Test
    fun `build - can build a basic flow authentication request without state parameter`() {

        val authenticationRequest = subject
                .basic()
                .build()

        authenticationRequestAssert(authenticationRequest)
                .hasNoParameter("state")
    }

    @Test
    fun `build - can build a basic flow authentication request overrides redirect uri`() {

        val authenticationRequest = subject
                .basic()
                .overrideRedirectUri(DIFFERENT_CLIENT_REDIRECT_URI)
                .build()

        authenticationRequestAssert(
                authenticationRequest,
                redirectUri = DIFFERENT_CLIENT_REDIRECT_URI
        ).hasNoParameter("redirect_uri", CLIENT_REDIRECT_URI)
    }

    @Test
    fun `build - throws Exception when no flow is chosen`() {

        assertThatThrownBy {
            subject.build()
        }
                .isInstanceOf(OpenIdConnectException::class.java)
                .hasFieldOrPropertyWithValue("message", "Please choose a flow parameter")
    }

    @Test
    fun `build - can build a basic flow authentication request with many scopes`() {

        val authenticationRequest = subject
                .basic()
                .scope(setOf("openid", "email", "profile"))
                .build()

        authenticationRequestAssert(authenticationRequest, "openid email profile")
    }

    @Test
    fun `build - adds openid scope if one is not provided`() {

        val authenticationRequest = subject
                .basic()
                .scope(setOf("email", "profile"))
                .build()

        authenticationRequestAssert(authenticationRequest, "openid email profile")
    }

    @Test
    fun `build - can build a basic flow authentication request with prompts`() {

        val authenticationRequest = subject
                .basic()
                .prompt(setOf(Prompt.SelectAccount, Prompt.Consent))
                .build()

        authenticationRequestAssert(authenticationRequest)
                .hasParameter("prompt", "select_account consent")
    }

    @Test
    fun `build - throws Exception when more than one prompt is presented for 'none' prompt`() {

        assertThatThrownBy {
            subject
                    .basic()
                    .prompt(setOf(Prompt.None, Prompt.Login))
                    .build()
        }
                .isInstanceOf(OpenIdConnectException::class.java)
                .hasFieldOrPropertyWithValue("message", "prompt 'none' cannot be provided with any other value")
    }

    @Test
    fun `build - can build a basic flow authentication request with optional parameters`() {

        val authenticationRequest = subject
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
                                            scopes: String = "openid",
                                            redirectUri: String = CLIENT_REDIRECT_URI): AbstractUriAssert<*> {

        return assertThat(URI.create(authenticationRequest.authorizeUrl))
                .hasHost("localhost")
                .hasPort(8089)
                .hasPath("/authorize")
                .hasScheme("http")
                .hasParameter("client_id", CLIENT_ID)
                .hasParameter("redirect_uri", redirectUri)
                .hasParameter("response_type", "code")
                .hasParameter("scope", scopes)
    }
}
