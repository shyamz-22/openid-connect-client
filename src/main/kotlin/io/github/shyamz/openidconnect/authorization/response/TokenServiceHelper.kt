package io.github.shyamz.openidconnect.authorization.response

import io.github.shyamz.openidconnect.authorization.response.model.AuthorizationCodeGrant
import io.github.shyamz.openidconnect.token.BasicFlowResponse
import io.github.shyamz.openidconnect.token.TokenService

data class TokenServiceHelper(private val tokenService: TokenService,
                              private val authorizationCodeGrant: AuthorizationCodeGrant) {

    fun exchange(): BasicFlowResponse {
        return tokenService.exchange(authorizationCodeGrant)
    }
}