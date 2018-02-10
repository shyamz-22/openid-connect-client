package io.github.shyamz.openidconnect.authorization.request

import javax.servlet.http.HttpServletResponse

data class AuthorizationRequest(val authorizeUrl: String) {

    fun andRedirect(response: HttpServletResponse) {
        return response.sendRedirect(authorizeUrl)
    }
}