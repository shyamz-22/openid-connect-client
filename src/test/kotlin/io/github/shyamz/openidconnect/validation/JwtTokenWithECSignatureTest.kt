package io.github.shyamz.openidconnect.validation

import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.github.shyamz.openidconnect.TestConstants.loadClientConfiguration
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod
import io.github.shyamz.openidconnect.mocks.stubForKeysEndpoint
import io.github.shyamz.openidconnect.mocks.stubForMockIdentityProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


class JwtTokenWithECSignatureTest {

    @JvmField
    @Rule
    val wireMockRule = WireMockRule(8089)

    private lateinit var publicKey: ECPublicKey
    private lateinit var privateKey: ECPrivateKey
    private lateinit var keyId: String
    private lateinit var jwKeySet: JWKSet

    @Before
    fun setUp() {
        stubForMockIdentityProvider()
        loadClientConfiguration("http://localhost:8089", TokenEndPointAuthMethod.Basic)
        createKeys()
        createJwkSet()
        stubForKeysEndpoint(jwKeySet)
    }

    @Test
    fun `can validate a valid IdToken`() {

        val idToken = JWTClaimsSet.Builder()
                .subject("user-id")
                .audience("client-id")
                .issuer("http://localhost:8089")
                .expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .issueTime(Date())
                .build()
                .toIdToken()

        val claims = JwtToken(idToken).claims()

        assertThat(claims).isNotEmpty
        assertThat(claims["sub"]).isEqualTo("user-id")
    }

    fun JWTClaimsSet.toIdToken(): String {
        val jwsHeader = JWSHeader.Builder(JWSAlgorithm.ES256).keyID(keyId).build()

        return SignedJWT(jwsHeader, this).apply {
            sign(ECDSASigner(privateKey))
        }.serialize()
    }


    fun createKeys() {
        val keyPair = KeyPairGenerator.getInstance("EC").also {
            it.initialize(Curve.P_256.toECParameterSpec())
        }.generateKeyPair()

        publicKey = keyPair.public as ECPublicKey
        privateKey = keyPair.private as ECPrivateKey
    }

    fun createJwkSet() {
        keyId = UUID.randomUUID().toString()

        val jwk = ECKey.Builder(Curve.P_256, publicKey)
                .keyID(keyId)
                .build()

        jwKeySet = JWKSet(jwk)

        println(jwKeySet.toPublicJWKSet().toJSONObject().toString())
    }
}