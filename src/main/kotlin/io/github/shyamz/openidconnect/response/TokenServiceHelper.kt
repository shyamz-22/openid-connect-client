package io.github.shyamz.openidconnect.response

import io.github.shyamz.openidconnect.authorization.request.AuthorizationCodeGrant
import io.github.shyamz.openidconnect.response.model.BasicFlowResponse
import io.github.shyamz.openidconnect.token.TokenService

data class TokenServiceHelper(private val tokenService: TokenService,
                              private val authorizationCodeGrant: AuthorizationCodeGrant) {

    fun exchange(): BasicFlowResponse {
        return tokenService.exchange(authorizationCodeGrant)
    }
}