package io.github.shyamz.openidconnect.response

import io.github.shyamz.openidconnect.TestConstants.AUTH_CODE_VALUE
import io.github.shyamz.openidconnect.TestConstants.BLANK
import io.github.shyamz.openidconnect.TestConstants.CLIENT_REDIRECT_URI
import io.github.shyamz.openidconnect.TestConstants.CLIENT_STATE_VALUE
import io.github.shyamz.openidconnect.TestConstants.DIFFERENT_CLIENT_STATE_VALUE
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import io.github.shyamz.openidconnect.mocks.MockHttpServletRequest
import io.github.shyamz.openidconnect.response.model.ErrorResponse
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

class OpenIdConnectCallBackInterceptorTest {

    private val defaultQueryParams = mapOf(
            "code" to arrayOf(AUTH_CODE_VALUE),
            "state" to arrayOf(CLIENT_STATE_VALUE)
    )

    @Test
    fun `extractAuthorizationCode - can extract code`() {

        //GIVEN
        val mockHttpServletRequest = MockHttpServletRequest(CLIENT_REDIRECT_URI, defaultQueryParams)

        //WHEN
        val basicFlowResponse = OpenIdConnectCallBackInterceptor(mockHttpServletRequest)
                .extractCode(CLIENT_STATE_VALUE)

        //THEN
        assertThat(basicFlowResponse).isNotNull()
                .hasFieldOrPropertyWithValue("code", AUTH_CODE_VALUE)
    }

    @Test
    fun `extractAuthorizationCode - when no state is returned`() {

        //GIVEN
        val queryParams = mapOf(
                "code" to arrayOf(AUTH_CODE_VALUE)
        )

        val mockHttpServletRequest = MockHttpServletRequest(
                CLIENT_REDIRECT_URI,
                queryParams)

        //WHEN
        val basicFlowResponse = OpenIdConnectCallBackInterceptor(mockHttpServletRequest)
                .extractCode()

        //THEN
        assertThat(basicFlowResponse).isNotNull()
                .hasFieldOrPropertyWithValue("code", AUTH_CODE_VALUE)
    }

    @Test
    fun `extractAuthorizationCode - throws exception when state don't match`() {

        //GIVEN
        val mockHttpServletRequest = MockHttpServletRequest(CLIENT_REDIRECT_URI, defaultQueryParams)

        //WHEN
        val resultWithException = assertThatThrownBy {
            OpenIdConnectCallBackInterceptor(mockHttpServletRequest)
                    .extractCode(DIFFERENT_CLIENT_STATE_VALUE)
        }

        //THEN
        resultWithException
                .isInstanceOf(OpenIdConnectException::class.java)
                .hasFieldOrPropertyWithValue("message", "Expected {\"state\": \"$CLIENT_STATE_VALUE\"} returned by " +
                        "IdP to equal locally cached {\"state\": \"$DIFFERENT_CLIENT_STATE_VALUE\"}")
    }


    @Test
    fun `extractAuthorizationCode - throws exception when code is empty`() {

        //GIVEN
        val queryParams = mapOf(
                "code" to arrayOf(BLANK),
                "state" to arrayOf(CLIENT_STATE_VALUE)
        )

        val mockHttpServletRequest = MockHttpServletRequest(CLIENT_REDIRECT_URI,
                queryParams)

        //WHEN
        val resultWithException = assertThatThrownBy {
            OpenIdConnectCallBackInterceptor(mockHttpServletRequest)
                    .extractCode(CLIENT_STATE_VALUE)
        }

        //THEN
        resultWithException
                .isInstanceOf(OpenIdConnectException::class.java)
                .hasFieldOrPropertyWithValue("message", "IdP returned empty or blank {\"authorization code\": \"$BLANK\"}")
    }

    @Test
    fun `extractAuthorizationCode - throws exception when authentication request fails`() {

        //GIVEN
        val queryParams = mapOf(
                "error" to arrayOf("invalid_request"),
                "error_description" to arrayOf("The request is missing a required parameter"),
                "error_uri" to arrayOf("https://tools.ietf.org/html/rfc6749#section-4.2.1"),
                "state" to arrayOf(CLIENT_STATE_VALUE)
        )

        val mockHttpServletRequest = MockHttpServletRequest(CLIENT_REDIRECT_URI,
                queryParams)

        //WHEN
        val resultWithException = assertThatThrownBy {
            OpenIdConnectCallBackInterceptor(mockHttpServletRequest)
                    .extractCode(CLIENT_STATE_VALUE)
        }

        //THEN
        resultWithException
                .isInstanceOf(OpenIdConnectException::class.java)
                .hasFieldOrPropertyWithValue("errorResponse", ErrorResponse(
                        "invalid_request",
                        "The request is missing a required parameter",
                        "https://tools.ietf.org/html/rfc6749#section-4.2.1"))
    }
}