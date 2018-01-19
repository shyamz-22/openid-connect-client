package io.github.shyamz.openidconnect.authorization.request

import javax.servlet.http.HttpServletResponse

data class AuthorizationRequest(val authorizeUrl: String) {

    fun redirect(response: HttpServletResponse) {
        return AuthorizationRequestor(authorizeUrl, response).makeRequest()
    }
}