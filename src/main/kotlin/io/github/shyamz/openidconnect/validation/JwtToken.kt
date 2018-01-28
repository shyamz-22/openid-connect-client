package io.github.shyamz.openidconnect.validation

import com.nimbusds.jwt.SignedJWT
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import java.text.ParseException

internal class JwtToken(private val idToken: String,
                        private val nonce: String? = null) {

    fun claims(): Map<String, Any> {

        return try {
            SignedJWT
                    .parse(idToken)
                    .verifySignature()
                    .validateIssuer()
                    .validateAudience()
                    .validateAudienceHasAuthorizedParty()
                    .validateAuthorizedParty()
                    .validateTokenExpiry()
                    .validateNonce(nonce)
                    .validateIssuedAt()
                    .validateTimeElapsedSinceLastAuthentication()
                    .claims
        } catch (e: ParseException) {
            throw OpenIdConnectException("'$idToken' is an invalid JWT token")
        }

    }
}

