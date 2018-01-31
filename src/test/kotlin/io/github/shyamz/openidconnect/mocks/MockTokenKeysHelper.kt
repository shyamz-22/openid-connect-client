package io.github.shyamz.openidconnect.mocks

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

object MockTokenKeysHelper {

    private val keyId = UUID.randomUUID().toString()
    private val keyPair = createRsaKeys()
    val idToken = createIdToken(keyId, keyPair.private as RSAPrivateKey)
    val jwkKeySet = createJwkSet(keyId, keyPair.public as RSAPublicKey)

    private fun createRsaKeys(): KeyPair {
        return KeyPairGenerator.getInstance("RSA").apply {
            initialize(1024)
        }.genKeyPair()
    }

    private fun createJwkSet(keyId: String, publicKey: RSAPublicKey): JWKSet {

        return with(RSAKey.Builder(publicKey)
                .keyID(keyId)
                .build()) {
            JWKSet(this)
        }
    }

    private fun createIdToken(keyId: String, rsaPrivateKey: RSAPrivateKey): String {

        val claims = JWTClaimsSet.Builder()
                .subject("user-id")
                .audience("client-id")
                .issuer("http://localhost:8089")
                .expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .issueTime(Date())
                .build()

        val jwsHeader = JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(keyId)
                .build()

        return SignedJWT(jwsHeader, claims).apply {
            sign(RSASSASigner(rsaPrivateKey))
        }.serialize()
    }

}