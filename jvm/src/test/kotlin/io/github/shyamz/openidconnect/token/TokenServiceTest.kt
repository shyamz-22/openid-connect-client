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
import io.github.shyamz.openidconnect.TestConstants.REFRESH_ERROR_RESPONSE
import io.github.shyamz.openidconnect.TestConstants.REFRESH_TOKEN_VALUE
import io.github.shyamz.openidconnect.TestConstants.loadClientConfiguration
import io.github.shyamz.openidconnect.TestConstants.tokenResponse
import io.github.shyamz.openidconnect.authorization.request.AuthorizationCodeGrant
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod.*
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import io.github.shyamz.openidconnect.mocks.stubForMockIdentityProvider
import io.github.shyamz.openidconnect.mocks.stubForTokenResponseWithBadRequest
import io.github.shyamz.openidconnect.mocks.stubForTokenResponseWithBasicAuth
import io.github.shyamz.openidconnect.mocks.stubForTokenResponseWithPostAuth
import io.github.shyamz.openidconnect.response.model.BasicFlowResponse
import io.github.shyamz.openidconnect.response.model.ErrorResponse
import org.assertj.core.api.Assertions.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Duration
import java.util.*


class TokenServiceTest {

    @Rule
    @JvmField
    val wireMockRule = WireMockRule(8089)

    private val mockIssuer = "http://localhost:8089"
    private lateinit var subject: TokenService

    @Before
    fun setUp() {
        stubForMockIdentityProvider()
        subject = TokenService()
    }


    @Test
    fun `exchange - can exchange tokens with basic authentication`() {
        //GIVEN
        stubForTokenResponseWithBasicAuth(tokenResponse())
        loadClientConfiguration(mockIssuer, Basic)

        //WHEN
        val basicFlowResponse = subject
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
        stubForTokenResponseWithPostAuth(tokenResponse())
        loadClientConfiguration(mockIssuer, Post)

        //WHEN
        val basicFlowResponse = subject
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
        loadClientConfiguration(mockIssuer, Basic)

        //WHEN
        val basicFlowResponseWithError = assertThatThrownBy {
            TokenService()
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
    fun `exchange - throws exception when authentication method is not supported`() {
        //GIVEN
        loadClientConfiguration(mockIssuer, None)

        //WHEN
        val basicFlowResponseWithError = assertThatThrownBy {
            TokenService()
                    .exchange(AuthorizationCodeGrant(AUTH_CODE_VALUE))
        }

        //THEN
        basicFlowResponseWithError
                .isInstanceOf(OpenIdConnectException::class.java)
                .hasFieldOrPropertyWithValue("message",
                        "Token Authentication method 'None' is not supported by IdP. " +
                                "Please choose one of [Post, Basic] values")


        verify(0, postRequestedFor(urlPathMatching("/token")))
    }

    @Test
    fun `refresh - can refresh tokens with basic authentication`() {
        //GIVEN
        stubForTokenResponseWithBasicAuth(tokenResponse(
                NEW_ID_TOKEN_VALUE,
                NEW_ACCESS_TOKEN_VALUE,
                NEW_REFRESH_TOKEN_VALUE
        ))
        loadClientConfiguration(mockIssuer, Basic)

        //WHEN
        val basicFlowResponse = subject
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
        stubForTokenResponseWithPostAuth(tokenResponse(
                NEW_ID_TOKEN_VALUE,
                NEW_ACCESS_TOKEN_VALUE,
                NEW_REFRESH_TOKEN_VALUE
        ))
        loadClientConfiguration(mockIssuer, Post)
        //WHEN
        val basicFlowResponse = subject
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
        stubForTokenResponseWithPostAuth(tokenResponse(
                NEW_ID_TOKEN_VALUE,
                NEW_ACCESS_TOKEN_VALUE,
                NEW_REFRESH_TOKEN_VALUE
        ))
        loadClientConfiguration(mockIssuer, Post)

        //WHEN
        val basicFlowResponse = subject
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
        loadClientConfiguration(mockIssuer, Basic)

        //WHEN
        val basicFlowResponseWithError = assertThatThrownBy {
            subject
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
        assertThat(Duration.between(Date().toInstant(), actual.expiresAt!!.toInstant()).seconds)
                .isCloseTo(3600, within(1L))
        assertThat(actual.accessToken).isEqualTo(expectedAccessToken)
        assertThat(actual.idToken).isEqualTo(expectedIdToken)
        assertThat(actual.refreshToken).isEqualTo(expectedRefreshToken)
    }

}