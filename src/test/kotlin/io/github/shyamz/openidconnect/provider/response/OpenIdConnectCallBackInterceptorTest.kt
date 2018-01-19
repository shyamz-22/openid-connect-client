package io.github.shyamz.openidconnect.provider.response

import io.github.shyamz.openidconnect.TestConstants.AUTH_CODE_VALUE
import io.github.shyamz.openidconnect.TestConstants.CLIENT_REDIRECT_URI
import io.github.shyamz.openidconnect.TestConstants.CLIENT_STATE_VALUE
import io.github.shyamz.openidconnect.TestConstants.DIFFERENT_CLIENT_STATE_VALUE
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import io.github.shyamz.openidconnect.mocks.MockHttpServletRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

class OpenIdConnectCallBackInterceptorTest {

    @Test
    fun `extractAuthorizationCode - can extract code`() {

        //GIVEN
        val queryParams = mapOf(
                "code" to arrayOf(AUTH_CODE_VALUE),
                "state" to arrayOf(CLIENT_STATE_VALUE)
        )
        val mockHttpServletRequest = MockHttpServletRequest(CLIENT_REDIRECT_URI,
                queryParams)

        //WHEN
        val basicFlowResponse = OpenIdConnectCallBackInterceptor(mockHttpServletRequest)
                .extractAuthorizationCode(CLIENT_STATE_VALUE)

        //THEN
        assertThat(basicFlowResponse).isNotNull()
                .hasFieldOrPropertyWithValue("authorizationCode", AUTH_CODE_VALUE)
    }

    @Test
    fun `extractAuthorizationCode - throws exception when state don't match`() {

        //GIVEN
        val queryParams = mapOf(
                "code" to arrayOf(AUTH_CODE_VALUE),
                "state" to arrayOf(CLIENT_STATE_VALUE)
        )

        val mockHttpServletRequest = MockHttpServletRequest(CLIENT_REDIRECT_URI,
                queryParams)

        //WHEN
        val resultWithException = assertThatThrownBy {
            OpenIdConnectCallBackInterceptor(mockHttpServletRequest)
                    .extractAuthorizationCode(DIFFERENT_CLIENT_STATE_VALUE)
        }

        //THEN
        resultWithException
                .isInstanceOf(OpenIdConnectException::class.java)
                .hasFieldOrPropertyWithValue("message", "Expected 'state' value '$CLIENT_STATE_VALUE' returned by " +
                        "IdP to equal locally cached 'state' value '$DIFFERENT_CLIENT_STATE_VALUE'")
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
                .extractAuthorizationCode()

        //THEN
        assertThat(basicFlowResponse).isNotNull()
                .hasFieldOrPropertyWithValue("authorizationCode", AUTH_CODE_VALUE)
    }
}