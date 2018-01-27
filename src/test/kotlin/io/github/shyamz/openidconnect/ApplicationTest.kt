package io.github.shyamz.openidconnect

import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.github.shyamz.openidconnect.TestConstants.AUTH_CODE_VALUE
import io.github.shyamz.openidconnect.TestConstants.CLIENT_REDIRECT_URI
import io.github.shyamz.openidconnect.TestConstants.CLIENT_STATE_VALUE
import io.github.shyamz.openidconnect.TestConstants.ID_TOKEN_VALUE
import io.github.shyamz.openidconnect.TestConstants.SUCCESSFUL_RESPONSE
import io.github.shyamz.openidconnect.TestConstants.loadClientConfiguration
import io.github.shyamz.openidconnect.authorization.request.AuthenticationRequestBuilder
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod.Basic
import io.github.shyamz.openidconnect.mocks.MockHttpServletRequest
import io.github.shyamz.openidconnect.mocks.MockHttpServletResponse
import io.github.shyamz.openidconnect.mocks.stubForMockIdentityProvider
import io.github.shyamz.openidconnect.mocks.stubForTokenResponseWithBasicAuth
import io.github.shyamz.openidconnect.response.OpenIdConnectCallBackInterceptor
import org.assertj.core.api.Assertions.assertThat
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
                .redirect(mockHttpServletResponse)

        mockHttpServletResponse.isRedirectedTo(AUTHORIZE_URL)
    }

    @Test
    fun `can exchange code for token fluently`() {
        stubForTokenResponseWithBasicAuth(SUCCESSFUL_RESPONSE)

        val mockHttpServletRequest = MockHttpServletRequest(CLIENT_REDIRECT_URI,
                mapOf("code" to arrayOf(AUTH_CODE_VALUE),
                        "state" to arrayOf(CLIENT_STATE_VALUE)))

        val tokens = OpenIdConnectCallBackInterceptor(mockHttpServletRequest)
                .extractAuthorizationCode(CLIENT_STATE_VALUE)
                .exchange()

        assertThat(tokens.idToken).isEqualTo(ID_TOKEN_VALUE)
    }

    companion object {
        private val AUTHORIZE_URL = "http://localhost:8089/authorize?client_id=client-id&redirect_uri=https%3A%2F%2Fopenidconnect.net%2Fcallback&response_type=code&scope=openid"
    }
}

