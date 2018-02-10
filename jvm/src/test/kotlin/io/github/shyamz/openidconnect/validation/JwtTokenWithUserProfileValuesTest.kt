package io.github.shyamz.openidconnect.validation

import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader.Builder
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.github.shyamz.openidconnect.StandardClaims.address
import io.github.shyamz.openidconnect.StandardClaims.birthdate
import io.github.shyamz.openidconnect.StandardClaims.country
import io.github.shyamz.openidconnect.StandardClaims.email
import io.github.shyamz.openidconnect.StandardClaims.email_verified
import io.github.shyamz.openidconnect.StandardClaims.family_name
import io.github.shyamz.openidconnect.StandardClaims.formatted
import io.github.shyamz.openidconnect.StandardClaims.gender
import io.github.shyamz.openidconnect.StandardClaims.given_name
import io.github.shyamz.openidconnect.StandardClaims.locale
import io.github.shyamz.openidconnect.StandardClaims.locality
import io.github.shyamz.openidconnect.StandardClaims.middle_name
import io.github.shyamz.openidconnect.StandardClaims.name
import io.github.shyamz.openidconnect.StandardClaims.nickname
import io.github.shyamz.openidconnect.StandardClaims.phone_number
import io.github.shyamz.openidconnect.StandardClaims.phone_number_verified
import io.github.shyamz.openidconnect.StandardClaims.picture
import io.github.shyamz.openidconnect.StandardClaims.postal_code
import io.github.shyamz.openidconnect.StandardClaims.preferred_username
import io.github.shyamz.openidconnect.StandardClaims.profile
import io.github.shyamz.openidconnect.StandardClaims.region
import io.github.shyamz.openidconnect.StandardClaims.street_address
import io.github.shyamz.openidconnect.StandardClaims.updated_at
import io.github.shyamz.openidconnect.StandardClaims.website
import io.github.shyamz.openidconnect.StandardClaims.zoneinfo
import io.github.shyamz.openidconnect.TestConstants.USER_ID
import io.github.shyamz.openidconnect.TestConstants.loadClientConfiguration
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod
import io.github.shyamz.openidconnect.mocks.stubForKeysEndpoint
import io.github.shyamz.openidconnect.mocks.stubForMockIdentityProvider
import io.github.shyamz.openidconnect.response.model.*
import net.minidev.json.JSONObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.ChronoUnit.HOURS
import java.util.*


class JwtTokenWithUserProfileValuesTest {

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
        loadClientConfiguration("http://localhost:8089", TokenEndPointAuthMethod.Basic)
        SignatureVerifierFactory.cache.invalidateAll()
    }

    @After
    fun tearDown() {
        SignatureVerifierFactory.cache.invalidateAll()
    }

    @Test
    fun `can retrieve token with user profile name related claims`() {

        val idToken = basicToken()
                .claim(name, "Bob Alice")
                .claim(given_name, "Bob")
                .claim(family_name, "Alice")
                .claim(middle_name, "Bobby")
                .claim(nickname, "minion")
                .claim(preferred_username, "alice_98")
                .build()
                .toIdToken()

        val claims = JwtToken(idToken).claims()

        assertThat(claims.userInfo()).isEqualTo(UserInfo(Profile(userId = USER_ID,
                name = "Bob Alice",
                givenName = "Bob",
                familyName = "Alice",
                middleName = "Bobby",
                nickname = "minion",
                preferredUsername = "alice_98")))
    }

    @Test
    fun `can retrieve token with user profile related claims`() {

        val idToken = basicToken()
                .claim(profile, "http://plus.google.com/profile/1")
                .claim(picture, "http://plus.google.com/profile/1/pictures/1")
                .claim(website, "www.alicebob.com")
                .claim(gender, "female")
                .claim(birthdate, "1976-01-01")
                .claim(locale, "en-US")
                .claim(zoneinfo, "Europe/Paris")
                .claim(updated_at, A_DAY_BEFORE)
                .build()
                .toIdToken()

        val claims = JwtToken(idToken).claims()

        assertThat(claims.userInfo()).isEqualTo(UserInfo(Profile(userId = USER_ID,
                profile = "http://plus.google.com/profile/1",
                picture = "http://plus.google.com/profile/1/pictures/1",
                website = "www.alicebob.com",
                gender = "female",
                birthDate = "1976-01-01",
                locale = "en-US",
                zoneInfo = "Europe/Paris",
                updatedAt = A_DAY_BEFORE)))
    }

    @Test
    fun `can retrieve token with address related claims`() {

        val addr = mapOf(
                formatted to "134 Sesame Street \n 19876 Blitzberg, Germany",
                street_address to "Sesame Street",
                locality to "Blitzberg",
                region to "Upper Clarinet",
                postal_code to "19876",
                country to "Germany"
        )

        val idToken = basicToken()
                .claim(address, JSONObject(addr))
                .build()
                .toIdToken()

        val claims = JwtToken(idToken).claims()

        assertThat(claims.userInfo()).isEqualTo(UserInfo(
                profile = Profile(userId = USER_ID),
                address = Address(
                        formatted = "134 Sesame Street \n 19876 Blitzberg, Germany",
                        streetAddress = "Sesame Street",
                        locality = "Blitzberg",
                        region = "Upper Clarinet",
                        postalCode = "19876",
                        country = "Germany")))
    }

    @Test
    fun `can retrieve token with email related claims`() {

        val idToken = basicToken()
                .claim(email, EMAIL)
                .claim(email_verified, true)
                .build()
                .toIdToken()

        val claims = JwtToken(idToken).claims()

        assertThat(claims.userInfo()).isEqualTo(UserInfo(
                profile = Profile(userId = USER_ID),
                email = Email(EMAIL, true)))
    }

    @Test
    fun `can retrieve token with phone related claims`() {

        val idToken = basicToken()
                .claim(phone_number, PHONE)
                .claim(phone_number_verified, true)
                .build()
                .toIdToken()

        val claims = JwtToken(idToken).claims()

        assertThat(claims.userInfo()).isEqualTo(UserInfo(
                profile = Profile(userId = USER_ID),
                phoneNumber = PhoneNumber(PHONE, true)))
    }

    private fun basicToken(): JWTClaimsSet.Builder {
        return JWTClaimsSet.Builder()
                .subject("user-id")
                .audience("client-id")
                .issuer("http://localhost:8089")
                .expirationTime(Date.from(Instant.now().plus(1, HOURS)))
                .issueTime(Date())
    }


    private fun JWTClaimsSet.toIdToken(): String {
        val jwsHeader = Builder(JWSAlgorithm.RS256).keyID(keyId).build()

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

    companion object {
        private val A_DAY_BEFORE = Instant.now().minus(1, DAYS).epochSecond
        private val EMAIL = "user@gmail.com"
        private val PHONE = "+4945634564321"
    }
}
