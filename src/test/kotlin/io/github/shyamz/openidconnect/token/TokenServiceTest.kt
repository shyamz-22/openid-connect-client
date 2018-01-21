package io.github.shyamz.openidconnect.token

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.github.shyamz.openidconnect.TestConstants.ACCESS_TOKEN_VALUE
import io.github.shyamz.openidconnect.TestConstants.AUTH_CODE_VALUE
import io.github.shyamz.openidconnect.TestConstants.CLIENT_ID
import io.github.shyamz.openidconnect.TestConstants.CLIENT_SECRET
import io.github.shyamz.openidconnect.TestConstants.ERROR_RESPONSE
import io.github.shyamz.openidconnect.TestConstants.ID_TOKEN_VALUE
import io.github.shyamz.openidconnect.TestConstants.INVALID_CODE_VALUE
import io.github.shyamz.openidconnect.TestConstants.NEW_ACCESS_TOKEN_VALUE
import io.github.shyamz.openidconnect.TestConstants.NEW_ID_TOKEN_VALUE
import io.github.shyamz.openidconnect.TestConstants.NEW_REFRESH_TOKEN_VALUE
import io.github.shyamz.openidconnect.TestConstants.OPEN_ID_CLIENT
import io.github.shyamz.openidconnect.TestConstants.REFRESH_ERROR_RESPONSE
import io.github.shyamz.openidconnect.TestConstants.REFRESH_TOKEN_VALUE
import io.github.shyamz.openidconnect.TestConstants.SUCCESSFUL_REFRESH_RESPONSE
import io.github.shyamz.openidconnect.TestConstants.SUCCESSFUL_RESPONSE
import io.github.shyamz.openidconnect.authorization.request.AuthorizationCodeGrant
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod.Basic
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod.Post
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import io.github.shyamz.openidconnect.mocks.MockIdentityProviderConfiguration
import io.github.shyamz.openidconnect.mocks.stubForTokenResponseWithBadRequest
import io.github.shyamz.openidconnect.mocks.stubForTokenResponseWithBasicAuth
import io.github.shyamz.openidconnect.mocks.stubForTokenResponseWithPostAuth
import io.github.shyamz.openidconnect.response.model.BasicFlowResponse
import io.github.shyamz.openidconnect.response.model.ErrorResponse
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Rule
import org.junit.Test


class TokenServiceTest {

    @Rule
    @JvmField
    val wireMockRule = WireMockRule(8089)

    @Test
    fun `exchange - can exchange tokens with basic authentication`() {
        //GIVEN
        stubForTokenResponseWithBasicAuth(SUCCESSFUL_RESPONSE)
        val idProviderConfiguration = MockIdentityProviderConfiguration.get()

        //WHEN
        val basicFlowResponse = TokenService(idProviderConfiguration, OPEN_ID_CLIENT, Basic)
                .exchange(AuthorizationCodeGrant(AUTH_CODE_VALUE))

        //THEN
        assertBasicFlowResponse(basicFlowResponse,
                expectedAccessToken = ACCESS_TOKEN_VALUE,
                expectedIdToken = ID_TOKEN_VALUE,
                expectedRefreshToken = REFRESH_TOKEN_VALUE)

        verifyTokenEndPointCalledWith("code=SplxlOBeZQQYbYS6WxSbIA" +
                "&grant_type=authorization_code" +
                "&redirect_uri=https%3A%2F%2Fopenidconnect.net%2Fcallback")
    }

    @Test
    fun `exchange - can exchange tokens with post authentication`() {
        //GIVEN
        stubForTokenResponseWithPostAuth(SUCCESSFUL_RESPONSE)
        val idProviderConfiguration = MockIdentityProviderConfiguration.get()

        //WHEN
        val basicFlowResponse = TokenService(idProviderConfiguration, OPEN_ID_CLIENT, Post)
                .exchange(AuthorizationCodeGrant(AUTH_CODE_VALUE))

        //THEN
        assertBasicFlowResponse(basicFlowResponse,
                expectedAccessToken = ACCESS_TOKEN_VALUE,
                expectedIdToken = ID_TOKEN_VALUE,
                expectedRefreshToken = REFRESH_TOKEN_VALUE)

        verifyTokenEndPointCalledWith("client_id=$CLIENT_ID" +
                "&client_secret=$CLIENT_SECRET" +
                "&code=SplxlOBeZQQYbYS6WxSbIA" +
                "&grant_type=authorization_code" +
                "&redirect_uri=https%3A%2F%2Fopenidconnect.net%2Fcallback")
    }


    @Test
    fun `exchange - throws exception when something fails`() {
        //GIVEN
        stubForTokenResponseWithBadRequest(ERROR_RESPONSE)
        val idProviderConfiguration = MockIdentityProviderConfiguration.get()

        //WHEN
        val basicFlowResponseWithError = assertThatThrownBy {
            TokenService(idProviderConfiguration, OPEN_ID_CLIENT, Basic)
                    .exchange(AuthorizationCodeGrant(INVALID_CODE_VALUE))
        }

        //THEN
        basicFlowResponseWithError
                .isInstanceOf(OpenIdConnectException::class.java)
                .hasFieldOrPropertyWithValue("errorResponse",
                        ErrorResponse("invalid_grant",
                                "Authorization code is invalid or expired",
                                "https://tools.ietf.org/html/rfc6749#section-5.2"))


        verifyTokenEndPointCalledWith("code=$INVALID_CODE_VALUE" +
                "&grant_type=authorization_code" +
                "&redirect_uri=https%3A%2F%2Fopenidconnect.net%2Fcallback")
    }

    @Test
    fun `refresh - can refresh tokens with basic authentication`() {
        //GIVEN
        stubForTokenResponseWithBasicAuth(SUCCESSFUL_REFRESH_RESPONSE)
        val idProviderConfiguration = MockIdentityProviderConfiguration.get()

        //WHEN
        val basicFlowResponse = TokenService(idProviderConfiguration, OPEN_ID_CLIENT, Basic)
                .refresh(RefreshTokenGrant(REFRESH_TOKEN_VALUE))

        //THEN
        assertBasicFlowResponse(basicFlowResponse,
                expectedAccessToken = NEW_ACCESS_TOKEN_VALUE,
                expectedIdToken = NEW_ID_TOKEN_VALUE,
                expectedRefreshToken = NEW_REFRESH_TOKEN_VALUE)

        verifyTokenEndPointCalledWith("grant_type=refresh_token" +
                "&redirect_uri=https%3A%2F%2Fopenidconnect.net%2Fcallback" +
                "&refresh_token=8xLOxBtZp8")
    }

    @Test
    fun `refresh - can refresh tokens with post authentication`() {
        //GIVEN
        stubForTokenResponseWithPostAuth(SUCCESSFUL_REFRESH_RESPONSE)
        val idProviderConfiguration = MockIdentityProviderConfiguration.get()

        //WHEN
        val basicFlowResponse = TokenService(idProviderConfiguration, OPEN_ID_CLIENT, Post)
                .refresh(RefreshTokenGrant(REFRESH_TOKEN_VALUE))

        //THEN
        assertBasicFlowResponse(basicFlowResponse,
                expectedAccessToken = NEW_ACCESS_TOKEN_VALUE,
                expectedIdToken = NEW_ID_TOKEN_VALUE,
                expectedRefreshToken = NEW_REFRESH_TOKEN_VALUE)

        verifyTokenEndPointCalledWith("client_id=$CLIENT_ID" +
                "&client_secret=$CLIENT_SECRET" +
                "&grant_type=refresh_token" +
                "&redirect_uri=https%3A%2F%2Fopenidconnect.net%2Fcallback" +
                "&refresh_token=8xLOxBtZp8")
    }

    @Test
    fun `refresh - can refresh tokens with additional scope params`() {
        //GIVEN
        stubForTokenResponseWithPostAuth(SUCCESSFUL_REFRESH_RESPONSE)
        val idProviderConfiguration = MockIdentityProviderConfiguration.get()

        //WHEN
        val basicFlowResponse = TokenService(idProviderConfiguration, OPEN_ID_CLIENT, Post)
                .refresh(RefreshTokenGrant(REFRESH_TOKEN_VALUE), setOf("openid", "email", "profile"))

        //THEN
        assertBasicFlowResponse(basicFlowResponse,
                expectedAccessToken = NEW_ACCESS_TOKEN_VALUE,
                expectedIdToken = NEW_ID_TOKEN_VALUE,
                expectedRefreshToken = NEW_REFRESH_TOKEN_VALUE)

        verifyTokenEndPointCalledWith("client_id=$CLIENT_ID" +
                "&client_secret=$CLIENT_SECRET" +
                "&grant_type=refresh_token" +
                "&redirect_uri=https%3A%2F%2Fopenidconnect.net%2Fcallback" +
                "&refresh_token=8xLOxBtZp8" +
                "&scope=openid+email+profile")
    }

    @Test
    fun `refresh - throws exception when something fails`() {
        //GIVEN
        stubForTokenResponseWithBadRequest(REFRESH_ERROR_RESPONSE)
        val idProviderConfiguration = MockIdentityProviderConfiguration.get()

        //WHEN
        val basicFlowResponseWithError = assertThatThrownBy {
            TokenService(idProviderConfiguration, OPEN_ID_CLIENT, Basic)
                    .refresh(RefreshTokenGrant(ACCESS_TOKEN_VALUE))
        }

        //THEN
        basicFlowResponseWithError
                .isInstanceOf(OpenIdConnectException::class.java)
                .hasFieldOrPropertyWithValue("errorResponse",
                        ErrorResponse("invalid_grant",
                                "Refresh token is invalid or expired",
                                "https://tools.ietf.org/html/rfc6749#section-5.2"))


        verifyTokenEndPointCalledWith("grant_type=refresh_token" +
                "&redirect_uri=https%3A%2F%2Fopenidconnect.net%2Fcallback" +
                "&refresh_token=$ACCESS_TOKEN_VALUE")
    }

    private fun verifyTokenEndPointCalledWith(requestBody: String) {
        verify(postRequestedFor(urlPathMatching("/token")).withRequestBody(equalTo(requestBody)))
    }

    private fun assertBasicFlowResponse(actual: BasicFlowResponse,
                                        expectedAccessToken: String,
                                        expectedIdToken: String,
                                        expectedRefreshToken: String) {

        assertThat(actual.tokenType).isEqualTo("Bearer")
        assertThat(actual.expiresIn).isEqualTo(3600)
        assertThat(actual.accessToken).isEqualTo(expectedAccessToken)
        assertThat(actual.idToken).isEqualTo(expectedIdToken)
        assertThat(actual.refreshToken).isEqualTo(expectedRefreshToken)
    }
}