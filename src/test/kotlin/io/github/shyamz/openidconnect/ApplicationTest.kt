package io.github.shyamz.openidconnect

import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.github.shyamz.openidconnect.TestConstants.AUTH_CODE_VALUE
import io.github.shyamz.openidconnect.TestConstants.CLIENT_ID
import io.github.shyamz.openidconnect.TestConstants.CLIENT_REDIRECT_URI
import io.github.shyamz.openidconnect.TestConstants.CLIENT_STATE_VALUE
import io.github.shyamz.openidconnect.TestConstants.USER_ID
import io.github.shyamz.openidconnect.TestConstants.loadClientConfiguration
import io.github.shyamz.openidconnect.TestConstants.tokenResponse
import io.github.shyamz.openidconnect.authorization.request.AuthenticationRequestBuilder
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod.Basic
import io.github.shyamz.openidconnect.mocks.*
import io.github.shyamz.openidconnect.mocks.MockTokenKeysHelper.idToken
import io.github.shyamz.openidconnect.mocks.MockTokenKeysHelper.jwkKeySet
import io.github.shyamz.openidconnect.response.OpenIdConnectCallBackInterceptor
import io.github.shyamz.openidconnect.response.model.ClientInfo
import io.github.shyamz.openidconnect.response.model.Profile
import io.github.shyamz.openidconnect.response.model.UserInfo
import io.github.shyamz.openidconnect.validation.SignatureVerifierFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
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
        SignatureVerifierFactory.cache.invalidateAll()
    }

    @After
    fun tearDown() {
        SignatureVerifierFactory.cache.invalidateAll()
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
        stubForKeysEndpoint(jwkKeySet)
        stubForTokenResponseWithBasicAuth(tokenResponse(idToken = idToken))

        val mockHttpServletRequest = MockHttpServletRequest(CLIENT_REDIRECT_URI,
                mapOf("code" to arrayOf(AUTH_CODE_VALUE),
                        "state" to arrayOf(CLIENT_STATE_VALUE)))

        val user = OpenIdConnectCallBackInterceptor(mockHttpServletRequest)
                .extractAuthorizationCode(CLIENT_STATE_VALUE)
                .exchangeCodeForTokens()
                .extractAuthenticatedUserInfo()

        assertThat(user.tokens).isNotNull()
        assertThat(user.clientInfo).isEqualTo(ClientInfo(CLIENT_ID))
        assertThat(user.userInfo).isEqualTo(UserInfo(Profile(USER_ID)))

    }

    companion object {
        private val AUTHORIZE_URL = "http://localhost:8089/authorize?client_id=client-id&response_type=code&scope=openid&redirect_uri=https%3A%2F%2Fopenidconnect.net%2Fcallback"
    }
}

