package io.github.shyamz.openidconnect

import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.github.shyamz.openidconnect.TestConstants.AUTH_CODE_VALUE
import io.github.shyamz.openidconnect.TestConstants.CLIENT_REDIRECT_URI
import io.github.shyamz.openidconnect.TestConstants.CLIENT_STATE_VALUE
import io.github.shyamz.openidconnect.TestConstants.loadClientConfiguration
import io.github.shyamz.openidconnect.TestConstants.tokenResponse
import io.github.shyamz.openidconnect.authorization.request.AuthenticationRequestBuilder
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod.Basic
import io.github.shyamz.openidconnect.mocks.*
import io.github.shyamz.openidconnect.response.OpenIdConnectCallBackInterceptor
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ApplicationTest {

    @Rule
    @JvmField
    val wireMockRule = WireMockRule(8089)

    @Before
    fun setUp() {
        stubForMockIdentityProvider()
        loadClientConfiguration("http://localhost:8089", Basic)
    }

    @Test
    fun `can build authentication request fluently`() {

        val authorizeRequest = AuthenticationRequestBuilder()
                .basic()
                .build()

        assertThat(authorizeRequest.authorizeUrl).isEqualTo(AUTHORIZE_URL)
    }

    @Test
    fun `can redirect to authentication request fluently`() {

        val mockHttpServletResponse = MockHttpServletResponse()

        AuthenticationRequestBuilder()
                .basic()
                .build()
                .andRedirect(mockHttpServletResponse)

        mockHttpServletResponse.isRedirectedTo(AUTHORIZE_URL)
    }

    @Test
    fun `can exchange code for token fluently`() {
        stubForKeysEndpoint(MockTokenKeysHelper.jwkKeySet)
        stubForTokenResponseWithBasicAuth(tokenResponse(idToken = MockTokenKeysHelper.idToken))

        val mockHttpServletRequest = MockHttpServletRequest(CLIENT_REDIRECT_URI,
                mapOf("code" to arrayOf(AUTH_CODE_VALUE),
                        "state" to arrayOf(CLIENT_STATE_VALUE)))

        val user = OpenIdConnectCallBackInterceptor(mockHttpServletRequest)
                .extractAuthorizationCode(CLIENT_STATE_VALUE)
                .exchange()
                .authenticatedUser()

        assertThat(user.basicFlowResponse).isNotNull()
        assertThat(user.claims).isNotEmpty
        assertThat(user.claims).contains(
                entry("sub", "user-id"),
                entry("aud", listOf("client-id"))
        )
    }

    companion object {
        private val AUTHORIZE_URL = "http://localhost:8089/authorize?client_id=client-id&response_type=code&scope=openid&redirect_uri=https%3A%2F%2Fopenidconnect.net%2Fcallback"
    }
}

