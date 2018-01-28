package io.github.shyamz.openidconnect.validation

import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSObject
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jwt.util.DateUtils
import io.github.shyamz.openidconnect.configuration.ClientConfiguration
import io.github.shyamz.openidconnect.configuration.model.SigningAlgorithm
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import java.time.Duration
import java.time.Instant
import java.util.*

internal fun JWSHeader.signingAlgorithm(): SigningAlgorithm {
    return ClientConfiguration.provider.idTokenSigningAlgorithms.firstOrNull {
        it.name == algorithm.name
    } ?: throw OpenIdConnectException("Malicious Token. IdP does not support Algorithm '$algorithm'")
}

internal fun JWTClaimsSet.validateTimeElapsedSinceLastAuthentication(): JWTClaimsSet {

    getDateClaim("auth_time")?.let {
        val elapsedTimeSinceLastAuthenticated = Duration.between(Instant.ofEpochMilli(it.time), Instant.now())
        if (elapsedTimeSinceLastAuthenticated.abs().seconds > 300) {
            throw OpenIdConnectException("User last authenticated was '${elapsedTimeSinceLastAuthenticated.abs().toMinutes()}' minutes before. Re authenticate the user")
        }
    }

    return this
}

internal fun JWTClaimsSet.validateNonce(nonce: String? = null): JWTClaimsSet {
    if (getStringClaim("nonce") != nonce) {
        throw OpenIdConnectException("Expected nonce '${getStringClaim("nonce")}' in id_token to match stored nonce '$nonce'")
    }
    return this
}

internal fun JWTClaimsSet.validateIssuedAt(): JWTClaimsSet {
    val issuedDuration = Duration.between(Instant.ofEpochMilli(issueTime.time), Instant.now())
    if (issuedDuration.abs().seconds > 120) {
        throw OpenIdConnectException("id_token is issued at '$issueTime' is too far away from the current time '${Date()}'")
    }
    return this
}

internal fun JWTClaimsSet.validateTokenExpiry(): JWTClaimsSet {
    if (DateUtils.isAfter(Date(), expirationTime, 60)) {
        throw OpenIdConnectException("id_token expired. Token expiration time is '$expirationTime'")
    }
    return this
}

internal fun JWTClaimsSet.validateAuthorizedParty(): JWTClaimsSet {
    if (getStringClaim("azp").isNullOrBlank().not() && getStringClaim("azp") != ClientConfiguration.client.id) {
        throw OpenIdConnectException("Expected azp '${getStringClaim("azp")}' in id_token to match client id '${ClientConfiguration.client.id}'")
    }
    return this
}

internal fun JWTClaimsSet.validateAudienceHasAuthorizedParty(): JWTClaimsSet {
    if (audience.size > 1 && getStringClaim("azp").isNullOrBlank()) {
        throw OpenIdConnectException("Expected id_token with multiple audiences '$audience' to have an 'azp' claim. But found none")
    }
    return this
}

internal fun JWTClaimsSet.validateAudience(): JWTClaimsSet {
    if (audience.contains(ClientConfiguration.client.id).not()) {
        throw OpenIdConnectException("Expected audience '$audience' in id_token to contain client '${ClientConfiguration.client.id}'")
    }

    return this
}

internal fun JWTClaimsSet.validateIssuer(): JWTClaimsSet {
    if (issuer != ClientConfiguration.provider.issuer.toString()) {
        throw OpenIdConnectException("Expected issuer '$issuer' in id_token to match well known config issuer '${ClientConfiguration.provider.issuer}'")
    }

    return this
}

internal fun SignedJWT.verifySignature(): JWTClaimsSet {
    if (!verify(SignatureVerifierFactory().jwsVerifier(this.header))) {
        throw OpenIdConnectException("Malicious Token. signature verification failed for token: \n'${this.serialize()}'")
    }
    return this.jwtClaimsSet
}
