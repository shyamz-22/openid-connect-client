package io.github.shyamz.openidconnect

import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.github.shyamz.openidconnect.TestConstants.AUTH_CODE_VALUE
import io.github.shyamz.openidconnect.TestConstants.CLIENT_ID
import io.github.shyamz.openidconnect.TestConstants.CLIENT_REDIRECT_URI
import io.github.shyamz.openidconnect.TestConstants.CLIENT_SECRET
import io.github.shyamz.openidconnect.TestConstants.CLIENT_STATE_VALUE
import io.github.shyamz.openidconnect.TestConstants.ID_TOKEN_VALUE
import io.github.shyamz.openidconnect.authorization.request.OpenIdClient
import io.github.shyamz.openidconnect.authorization.response.OpenIdConnectCallBackInterceptor
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod
import io.github.shyamz.openidconnect.discovery.WellKnownConfigDiscoverer
import io.github.shyamz.openidconnect.mocks.MockHttpServletRequest
import io.github.shyamz.openidconnect.mocks.MockHttpServletResponse
import io.github.shyamz.openidconnect.mocks.MockIdentityProviderConfiguration
import io.github.shyamz.openidconnect.mocks.stubForTokenResponseWithBasicAuth
import io.github.shyamz.openidconnect.token.TokenService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import java.net.URI

class ApplicationTest {

    @Rule
    @JvmField
    val wireMockRule = WireMockRule(8089)

    @Test
    fun `can build authentication request fluently`() {

        val authorizeRequest = WellKnownConfigDiscoverer(URI.create("https://accounts.google.com/"))
                .authorizeRequest(OpenIdClient("id", "http://redirect-uri"))
                .basic()
                .build()

        assertThat(authorizeRequest.authorizeUrl).isNotNull()
    }

    @Test
    fun `can redirect to authentication request fluently`() {

        val mockHttpServletResponse = MockHttpServletResponse()

        WellKnownConfigDiscoverer(URI.create("https://accounts.google.com/"))
                .authorizeRequest(OpenIdClient("id", "http://redirect-uri"))
                .basic()
                .build()
                .redirect(mockHttpServletResponse)

        assertThat(mockHttpServletResponse.isRedirectedTo(AUTHORIZE_URL)).isTrue()
    }

    @Test
    fun `can exchange code for token fluently`() {
        stubForTokenResponseWithBasicAuth()

        val mockHttpServletRequest = MockHttpServletRequest(CLIENT_REDIRECT_URI,
                mapOf("code" to arrayOf(AUTH_CODE_VALUE),
                        "state" to arrayOf(CLIENT_STATE_VALUE)))


        val tokenService = TokenService(MockIdentityProviderConfiguration.get(),
                                        OpenIdClient(CLIENT_ID, CLIENT_REDIRECT_URI, CLIENT_SECRET),
                                        TokenEndPointAuthMethod.Basic)


        val tokens = OpenIdConnectCallBackInterceptor(mockHttpServletRequest)
                .extractAuthorizationCode(CLIENT_STATE_VALUE)
                .withTokenService(tokenService)
                .exchange()

        assertThat(tokens.idToken).isEqualTo(ID_TOKEN_VALUE)
    }

    companion object {
        private val AUTHORIZE_URL = "https://accounts.google.com/o/oauth2/v2/auth?client_id=id&redirect_uri=http%3A%2F%2Fredirect-uri&response_type=code&scope=openid"
    }
}

