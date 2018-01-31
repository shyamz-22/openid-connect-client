package io.github.shyamz.openidconnect.validation

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.ECDSAVerifier
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import io.github.shyamz.openidconnect.configuration.ClientConfiguration
import io.github.shyamz.openidconnect.configuration.model.AlgorithmType.*
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import java.util.concurrent.TimeUnit

internal object SignatureVerifierFactory {

    internal val cache: LoadingCache<String, JWK> = Caffeine.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .refreshAfterWrite(12, TimeUnit.HOURS)
            .build<String, JWK> { keyId -> jwk(keyId) }

    internal fun jwsVerifier(header: JWSHeader): JWSVerifier {

        return when (header.signingAlgorithm()) {
            RSA -> {
                RSASSAVerifier(cache.get(header.keyID) as RSAKey)
            }
            ECDSA -> {
                ECDSAVerifier(cache.get(header.keyID) as ECKey)
            }
            HMAC -> {
                MACVerifier(ClientConfiguration.client.secret)
            }
        }
    }

    private fun jwk(keyId: String): JWK {
        return getPublicKeys().getKeyByKeyId(keyId)
                ?: throw OpenIdConnectException("unable to find key for id '$keyId' to validate tokens from '${ClientConfiguration.provider.jwksEndpoint}'")
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
