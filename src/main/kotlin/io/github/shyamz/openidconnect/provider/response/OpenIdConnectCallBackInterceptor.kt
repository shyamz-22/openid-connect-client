package io.github.shyamz.openidconnect.provider.response

import io.github.shyamz.openidconnect.configuration.model.ResponseType
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import io.github.shyamz.openidconnect.provider.model.BasicFlowResponse
import io.github.shyamz.openidconnect.provider.model.ErrorResponse
import javax.servlet.http.HttpServletRequest


class OpenIdConnectCallBackInterceptor(private val request: HttpServletRequest) {

    /**
     * Supports 'query' response mode for authorization code extraction
     *
     * @See: http://openid.net/specs/oauth-v2-multiple-response-types-1_0.html#ResponseModes
     */

    fun extractAuthorizationCode(storedState: String? = null): BasicFlowResponse {
        val authorizationCode = request.getParameter(ResponseType.Code.parameter)
        val state = request.getParameter("state")

        validateState(storedState, state)

        return authorizationCode?.let { BasicFlowResponse(it) }
                ?: throw authenticationRequestError(request)

    }

    private fun authenticationRequestError(request: HttpServletRequest): OpenIdConnectException {
        val error = request.getParameter("error") ?: "invalid_request"
        val errorUri = request.getParameter("error_uri")
        val errorDescription = request.getParameter("error_description")

        return OpenIdConnectException("Failed to complete authentication request",
                ErrorResponse(error, errorDescription, errorUri))
    }


    private fun validateState(storedState: String?, state: String?) {
        if (storedState != state) throw OpenIdConnectException("Expected {\"state\": \"$state\"} returned by " +
                "IdP to equal locally cached {\"state\": \"$storedState\"}")
    }
}

