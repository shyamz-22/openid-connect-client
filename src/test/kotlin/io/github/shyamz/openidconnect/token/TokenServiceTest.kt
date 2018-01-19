package io.github.shyamz.openidconnect.token

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.github.shyamz.openidconnect.TestConstants
import io.github.shyamz.openidconnect.TestConstants.AUTH_CODE_VALUE
import io.github.shyamz.openidconnect.TestConstants.CLIENT_ID
import io.github.shyamz.openidconnect.TestConstants.CLIENT_SECRET
import io.github.shyamz.openidconnect.TestConstants.OPEN_ID_CLIENT
import io.github.shyamz.openidconnect.authorization.response.model.AuthorizationCodeGrant
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod.Basic
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod.Post
import io.github.shyamz.openidconnect.mocks.MockIdentityProviderConfiguration
import io.github.shyamz.openidconnect.mocks.stubForTokenResponseWithBasicAuth
import io.github.shyamz.openidconnect.mocks.stubForTokenResponseWithPostAuth
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test


class TokenServiceTest {

    @Rule
    @JvmField
    val wireMockRule = WireMockRule(8089)

    @Test
    fun `exchange - can exchange tokens with basic authentication`() {

        stubForTokenResponseWithBasicAuth()

        val basicFlowResponse = TokenService(MockIdentityProviderConfiguration.get(), OPEN_ID_CLIENT, Basic)
                .exchange(AuthorizationCodeGrant(AUTH_CODE_VALUE))

        assertThat(basicFlowResponse.tokenType).isEqualTo("Bearer")
        assertThat(basicFlowResponse.expiresIn).isEqualTo(3600)
        assertThat(basicFlowResponse.accessToken).isEqualTo(TestConstants.ACCESS_TOKEN_VALUE)
        assertThat(basicFlowResponse.idToken).isEqualTo(TestConstants.ID_TOKEN_VALUE)
        assertThat(basicFlowResponse.refreshToken).isEqualTo(TestConstants.REFRESH_TOKEN_VALUE)

        verify(postRequestedFor(urlPathMatching("/token")).withRequestBody(
                equalTo("code=SplxlOBeZQQYbYS6WxSbIA&grant_type=AuthorizationCode" +
                "&redirect_uri=https%3A%2F%2Fopenidconnect.net%2Fcallback")))
    }

    @Test
    fun `exchange - can exchange tokens with post authentication`() {

        stubForTokenResponseWithPostAuth()

        val basicFlowResponse = TokenService(MockIdentityProviderConfiguration.get(), OPEN_ID_CLIENT, Post)
                .exchange(AuthorizationCodeGrant(AUTH_CODE_VALUE))

        assertThat(basicFlowResponse.tokenType).isEqualTo("Bearer")
        assertThat(basicFlowResponse.expiresIn).isEqualTo(3600)
        assertThat(basicFlowResponse.accessToken).isEqualTo(TestConstants.ACCESS_TOKEN_VALUE)
        assertThat(basicFlowResponse.idToken).isEqualTo(TestConstants.ID_TOKEN_VALUE)
        assertThat(basicFlowResponse.refreshToken).isEqualTo(TestConstants.REFRESH_TOKEN_VALUE)

        verify(postRequestedFor(urlPathMatching("/token")).withRequestBody(
                equalTo("client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET&code=SplxlOBeZQQYbYS6WxSbIA" +
                "&grant_type=AuthorizationCode&redirect_uri=https%3A%2F%2Fopenidconnect.net%2Fcallback")))
    }
}