package io.github.shyamz.openidconnect.discovery

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.shyamz.openidconnect.TestConstants.GOOGLE_ISSUER
import io.github.shyamz.openidconnect.TestConstants.PAYPAL_ISSUER
import io.github.shyamz.openidconnect.TestConstants.YAHOO_ISSUER
import io.github.shyamz.openidconnect.configuration.IdProviderConfiguration
import io.github.shyamz.openidconnect.configuration.model.CodeChallengeMethod.*
import io.github.shyamz.openidconnect.configuration.model.GrantType
import io.github.shyamz.openidconnect.configuration.model.ResponseType
import io.github.shyamz.openidconnect.configuration.model.ResponseType.*
import io.github.shyamz.openidconnect.configuration.model.SigningAlgorithm
import io.github.shyamz.openidconnect.configuration.model.SubjectType
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethodSupported.Basic
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethodSupported.Post
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

internal class WellKnownConfigDiscovererTest {

    private lateinit var wellknownConfigDiscoverer: WellKnownConfigDiscoverer

    @Test
    fun `getProviderConfiguration - loads google provider configuration`() {
        wellknownConfigDiscoverer = WellKnownConfigDiscoverer(GOOGLE_ISSUER)

        val google = wellknownConfigDiscoverer.identityProviderConfiguration()

        assertThat(google).isEqualTo(expectedIdPConfiguration("google"))
        assertThat(google.subjectTypes).isEqualTo(listOf(SubjectType.Public))
        assertThat(google.responseTypes).containsExactlyInAnyOrder(*ResponseType.values())
        assertThat(google.idTokenSigningAlgorithms).containsExactlyInAnyOrder(SigningAlgorithm.RS256)
        assertThat(google.scopes).containsExactlyInAnyOrder("openid", "email", "profile")
        assertThat(google.tokenEndpointAuthMethods).containsExactlyInAnyOrder(Basic, Post)
        assertThat(google.claims).containsExactlyInAnyOrder("aud",
                "email",
                "email_verified",
                "exp",
                "family_name",
                "given_name",
                "iat",
                "iss",
                "locale",
                "name",
                "picture",
                "sub")
        assertThat(google.codeChallengeMethods).containsExactlyInAnyOrder(Plain, S256)
        assertThat(google.grantTypes).isEmpty()
    }

    @Test
    fun `getProviderConfiguration - loads yahoo provider configuration`() {
        wellknownConfigDiscoverer = WellKnownConfigDiscoverer(YAHOO_ISSUER)

        val yahoo = wellknownConfigDiscoverer.identityProviderConfiguration()

        assertThat(yahoo).isEqualTo(expectedIdPConfiguration("yahoo"))
        assertThat(yahoo.subjectTypes).isEqualTo(listOf(SubjectType.Public))
        assertThat(yahoo.responseTypes).containsExactlyInAnyOrder(Code, Token, IdToken, CodeToken, CodeIdToken, TokenIdToken, CodeTokenIdToken)
        assertThat(yahoo.idTokenSigningAlgorithms).containsExactlyInAnyOrder(SigningAlgorithm.RS256, SigningAlgorithm.ES256)
        assertThat(yahoo.scopes).containsExactlyInAnyOrder("openid")
        assertThat(yahoo.tokenEndpointAuthMethods).containsExactlyInAnyOrder(Basic, Post)
        assertThat(yahoo.claims).containsExactlyInAnyOrder("aud",
                "email",
                "email_verified",
                "birthdate",
                "exp",
                "family_name",
                "given_name",
                "iat",
                "iss",
                "locale",
                "name",
                "sub",
                "auth_time")
        assertThat(yahoo.codeChallengeMethods).isEmpty()
        assertThat(yahoo.grantTypes).isEmpty()
    }

    @Test
    fun `getProviderConfiguration - loads paypal provider configuration`() {
        wellknownConfigDiscoverer = WellKnownConfigDiscoverer(PAYPAL_ISSUER)

        val paypal = wellknownConfigDiscoverer.identityProviderConfiguration()

        assertThat(paypal).isEqualTo(expectedIdPConfiguration("paypal"))
        assertThat(paypal.subjectTypes).isEqualTo(listOf(SubjectType.Pairwise))
        assertThat(paypal.responseTypes).containsExactlyInAnyOrder(Code, CodeIdToken)
        assertThat(paypal.idTokenSigningAlgorithms).containsExactlyInAnyOrder(SigningAlgorithm.RS256, SigningAlgorithm.HS256)
        assertThat(paypal.scopes).containsExactlyInAnyOrder(
                "email",
                "address",
                "phone",
                "openid",
                "profile",
                "https://uri.paypal.com/services/wallet/sendmoney",
                "https://uri.paypal.com/services/payments/futurepayments",
                "https://uri.paypal.com/services/expresscheckout")
        assertThat(paypal.tokenEndpointAuthMethods).containsExactlyInAnyOrder(Basic)
        assertThat(paypal.claims).containsExactlyInAnyOrder(
                "aud",
                "iss",
                "iat",
                "exp",
                "auth_time",
                "nonce",
                "sessionIndex",
                "user_id")
        assertThat(paypal.codeChallengeMethods).containsExactlyInAnyOrder(RS256, ES256, S256)
        assertThat(paypal.grantTypes).containsExactlyInAnyOrder(*GrantType.values())
    }

    private fun expectedIdPConfiguration(provider: String): IdProviderConfiguration {
        val resource = javaClass.getResource("/fixtures/$provider-openid-wellknown-config.json")
        return jacksonObjectMapper().readValue(resource,
                ProviderConfigurationModel::class.java).idProviderConfig()
    }
}