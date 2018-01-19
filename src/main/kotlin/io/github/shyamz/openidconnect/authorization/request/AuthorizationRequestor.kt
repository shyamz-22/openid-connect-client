package io.github.shyamz.openidconnect.authorization.request

import javax.servlet.http.HttpServletResponse


class AuthorizationRequestor(private val authorizeUrl: String,
                             private val response: HttpServletResponse) {

    fun makeRequest() {
        response.sendRedirect(authorizeUrl)
    }

}