package io.github.shyamz.openidconnect.validation

import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.ECDSAVerifier
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import io.github.shyamz.openidconnect.configuration.ClientConfiguration
import io.github.shyamz.openidconnect.configuration.model.SigningAlgorithm.*
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException

internal class SignatureVerifierFactory {

    fun jwsVerifier(header: JWSHeader): JWSVerifier {

        return when (header.signingAlgorithm()) {
            RS256 -> {
                RSASSAVerifier(getPublicKeys().getKeyByKeyId(header.keyID) as RSAKey)
            }
            ES256 -> {
                ECDSAVerifier(getPublicKeys().getKeyByKeyId(header.keyID) as ECKey)
            }
            HS256 -> {
                MACVerifier(ClientConfiguration.client.secret)
            }
        }
    }

    private fun getPublicKeys(): JWKSet {
        try {
            val connectTimeout = 100
            val readTimeout = 1000
            val sizeLimit = 10000
            return JWKSet.load(ClientConfiguration.provider.jwksEndpoint.toURL(), connectTimeout, readTimeout, sizeLimit)
        } catch (e: Exception) {
            throw OpenIdConnectException("unable to fetch keys to validate tokens from '${ClientConfiguration.provider.jwksEndpoint}'")
        }
    }
}