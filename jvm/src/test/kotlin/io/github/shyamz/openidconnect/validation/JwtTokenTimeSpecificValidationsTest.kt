package io.github.shyamz.openidconnect.validation

import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.github.shyamz.openidconnect.TestConstants.loadClientConfiguration
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import io.github.shyamz.openidconnect.mocks.stubForKeysEndpoint
import io.github.shyamz.openidconnect.mocks.stubForMockIdentityProvider
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class JwtTokenTimeSpecificValidationsTest {

    @JvmField
    @Rule
    val wireMockRule = WireMockRule(8089)

    private lateinit var publicKey: RSAPublicKey
    private lateinit var privateKey: RSAPrivateKey
    private lateinit var keyId: String
    private lateinit var jwKeySet: JWKSet

    @Before
    fun setUp() {
        stubForMockIdentityProvider()
        createKeys()
        createJwkSet()
        stubForKeysEndpoint(jwKeySet)
        SignatureVerifierFactory.cache.invalidateAll()
    }

    @After
    fun tearDown() {
        SignatureVerifierFactory.cache.invalidateAll()
    }

    @Test
    fun `throws exception when user last authenticated time is more than ten minutes`() {
        loadClientConfiguration(
                "http://localhost:8089",
                TokenEndPointAuthMethod.Basic,
                maxAgeSinceLastAuthenticated = 600 // 10 minutes
        )

        val sixMinutesBefore = Date.from(Instant.now().minus(11, ChronoUnit.MINUTES))
        val idToken = JWTClaimsSet.Builder()
                .subject("user-id")
                .audience(listOf("client-id", "another-client-id"))
                .issuer("http://localhost:8089")
                .expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .issueTime(Date())
                .claim("auth_time", sixMinutesBefore)
                .claim("azp", "client-id")
                .build()
                .toIdToken()

        assertThatThrownBy { JwtToken(idToken).claims() }
                .isInstanceOf(OpenIdConnectException::class.java)
                .hasMessageContaining("User last authenticated was '11' minutes before. Re authenticate the user")

    }

    @Test
    fun `does not throw exception when user last authenticated time is within 3 minutes`() {
        loadClientConfiguration(
                "http://localhost:8089",
                TokenEndPointAuthMethod.Basic,
                maxAgeSinceLastAuthenticated = 180 // 3 minutes
        )

        val twoMinutesBefore = Date.from(Instant.now().minus(2, ChronoUnit.MINUTES))
        val idToken = JWTClaimsSet.Builder()
                .subject("user-id")
                .audience(listOf("client-id", "another-client-id"))
                .issuer("http://localhost:8089")
                .expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .issueTime(Date())
                .claim("auth_time", twoMinutesBefore)
                .claim("azp", "client-id")
                .build()
                .toIdToken()

        assertThatCode { JwtToken(idToken).claims() }
                .doesNotThrowAnyException()

    }


    @Test
    fun `throws exception when issue time is too far away in future considering a 2 minutes skew`() {
        loadClientConfiguration(
                "http://localhost:8089",
                TokenEndPointAuthMethod.Basic,
                clockSkew = 120 // 2 minutes
        )

        val threeMinutesInFuture = Date.from(Instant.now().plus(3, ChronoUnit.MINUTES))
        val idToken = JWTClaimsSet.Builder()
                .subject("user-id")
                .audience(listOf("client-id", "another-client-id"))
                .issuer("http://localhost:8089")
                .expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .issueTime(threeMinutesInFuture)
                .claim("azp", "client-id")
                .build()
                .toIdToken()

        assertThatThrownBy { JwtToken(idToken).claims() }
                .isInstanceOf(OpenIdConnectException::class.java)
                .hasMessageContaining("is too far away from the current time")

    }

    @Test
    fun `does not throw exception when issue time is not too far away in future considering a 2 minutes skew`() {
        loadClientConfiguration(
                "http://localhost:8089",
                TokenEndPointAuthMethod.Basic,
                clockSkew = 180 // 2 minutes
        )

        val twoMinutesInFuture = Date.from(Instant.now().plus(2, ChronoUnit.MINUTES))
        val idToken = JWTClaimsSet.Builder()
                .subject("user-id")
                .audience(listOf("client-id", "another-client-id"))
                .issuer("http://localhost:8089")
                .expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .issueTime(twoMinutesInFuture)
                .claim("azp", "client-id")
                .build()
                .toIdToken()

        assertThatCode { JwtToken(idToken).claims() }
                .doesNotThrowAnyException()

    }

    private fun JWTClaimsSet.toIdToken(): String {
        val jwsHeader = JWSHeader.Builder(JWSAlgorithm.RS256).keyID(keyId).build()

        return SignedJWT(jwsHeader, this).apply {
            sign(RSASSASigner(privateKey))
        }.serialize()
    }

    private fun createKeys() {
        val keyGenerator = KeyPairGenerator.getInstance("RSA")
        keyGenerator.initialize(1024)

        val kp = keyGenerator.genKeyPair()
        publicKey = kp.public as RSAPublicKey
        privateKey = kp.private as RSAPrivateKey
    }

    private fun createJwkSet() {
        keyId = UUID.randomUUID().toString()

        val jwk = RSAKey.Builder(publicKey)
                .keyID(keyId)
                .build()

        jwKeySet = JWKSet(jwk)
    }

}