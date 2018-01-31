package io.github.shyamz.openidconnect.validation

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.github.shyamz.openidconnect.TestConstants.ONCE
import io.github.shyamz.openidconnect.TestConstants.loadClientConfiguration
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod.Basic
import io.github.shyamz.openidconnect.mocks.MockTokenKeysHelper.createIdToken
import io.github.shyamz.openidconnect.mocks.MockTokenKeysHelper.jwkKeySet
import io.github.shyamz.openidconnect.mocks.MockTokenKeysHelper.keyId
import io.github.shyamz.openidconnect.mocks.MockTokenKeysHelper.keyPair
import io.github.shyamz.openidconnect.mocks.stubForKeysEndpoint
import io.github.shyamz.openidconnect.mocks.stubForMockIdentityProvider
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.security.interfaces.RSAPrivateKey

class LoadsKeyFromCacheTest {

    @JvmField
    @Rule
    val wireMockRule = WireMockRule(8089)


    @Before
    fun setUp() {
        stubForMockIdentityProvider()
        loadClientConfiguration("http://localhost:8089", Basic)
        stubForKeysEndpoint(jwkKeySet)
        SignatureVerifierFactory.cache.invalidateAll()
    }

    @After
    fun tearDown() {
        SignatureVerifierFactory.cache.invalidateAll()
    }

    @Test
    fun `loads key from endpoint`() {
        val idToken = createIdToken(keyId, keyPair.private as RSAPrivateKey)
        JwtToken(idToken).claims()

        keysEndpointCalled(ONCE)
    }

    @Test
    fun `loads key from cache`() {
        val idToken = createIdToken(keyId, keyPair.private as RSAPrivateKey, "user-id")
        JwtToken(idToken).claims()

        val yetAnotherIdToken = createIdToken(keyId, keyPair.private as RSAPrivateKey, "different-user-id")
        JwtToken(yetAnotherIdToken).claims()

        val yetYetAnotherIdToken = createIdToken(keyId, keyPair.private as RSAPrivateKey, "very-different-user-id")
        JwtToken(yetYetAnotherIdToken).claims()

        keysEndpointCalled(ONCE)
    }

    private fun keysEndpointCalled(times: Int) {
        verify(times, getRequestedFor(urlPathMatching("/keys")))
    }
}