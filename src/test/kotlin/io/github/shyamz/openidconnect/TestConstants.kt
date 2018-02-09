package io.github.shyamz.openidconnect

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.shyamz.openidconnect.configuration.ClientConfiguration
import io.github.shyamz.openidconnect.configuration.IdProviderConfiguration
import io.github.shyamz.openidconnect.configuration.OpenIdClient
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod
import io.github.shyamz.openidconnect.discovery.ProviderConfigurationModel
import io.github.shyamz.openidconnect.discovery.WellKnownConfigDiscoverer
import java.net.URI

object TestConstants {

    const val CLIENT_REDIRECT_URI = "https://openidconnect.net/callback"
    const val DIFFERENT_CLIENT_REDIRECT_URI = "https://different.openidconnect.net/callback"
    const val CLIENT_ID = "client-id"
    const val CLIENT_SECRET = "EA083983BA28CC6A82A3BC8931AA7C83C44B16EBF04886C42355A2BA214F16F8"
    const val DIFFERENT_CLIENT_SECRET = "BE7AD0288068AFEC3F37451DB7B8C9E2BCBC966C7E0325B0340E0DF5D844CBF2"
    const val CLIENT_STATE_VALUE = "randomState"
    const val DIFFERENT_CLIENT_STATE_VALUE = "anotherRandomState"
    const val NONCE_VALUE = "aVeryRandomValue"
    const val AUTH_CODE_VALUE = "SplxlOBeZQQYbYS6WxSbIA"
    const val INVALID_CODE_VALUE = "somerandommeaninglesstextforIdp"
    const val USER_ID = "user-id"
    const val BLANK = "                           "

    val GOOGLE_ISSUER = URI.create("https://accounts.google.com/")
    val YAHOO_ISSUER = URI.create("https://api.login.yahoo.com")
    val PAYPAL_ISSUER = URI.create("https://www.paypal.com")

    val GOOGLE_PROVIDER = WellKnownConfigDiscoverer(GOOGLE_ISSUER).identityProviderConfiguration()

    internal val OPEN_ID_CLIENT = OpenIdClient(CLIENT_ID, CLIENT_REDIRECT_URI, CLIENT_SECRET)

    const val ID_TOKEN_VALUE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ"
    const val NEW_ID_TOKEN_VALUE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkFsaWNlIEJvYiIsImFkbWluIjp0cnVlfQ.Mm1mIXXnuRCtrksgReUM28uMn2wmE0-xYzRfakWA4Cw"
    const val ACCESS_TOKEN_VALUE = "SlAV32hkKG"
    const val NEW_ACCESS_TOKEN_VALUE = "TlAV78hkNG"
    const val REFRESH_TOKEN_VALUE = "8xLOxBtZp8"
    const val NEW_REFRESH_TOKEN_VALUE = "9yM1y9uAq9"
    const val ONCE = 1

    val ERROR_RESPONSE = """
{
  "error": "invalid_grant",
  "error_description": "Authorization code is invalid or expired",
  "error_uri": "https://tools.ietf.org/html/rfc6749#section-5.2"
}
    """.trimIndent()

    val REFRESH_ERROR_RESPONSE = """
{
  "error": "invalid_grant",
  "error_description": "Refresh token is invalid or expired",
  "error_uri": "https://tools.ietf.org/html/rfc6749#section-5.2"
}
    """.trimIndent()

    fun expectedIdPConfiguration(provider: String): IdProviderConfiguration {
        val resource = javaClass.getResource("/fixtures/$provider-openid-wellknown-config.json")
        return jacksonObjectMapper().readValue(resource,
                ProviderConfigurationModel::class.java).idProviderConfig()
    }

    fun mockIdPConfiguration(): String {
        val resource = javaClass.getResource("/fixtures/mock-openid-wellknown-config.json")
        return resource.readText()
    }

    fun loadClientConfiguration(
            issuer: String,
            tokenEndPointAuthMethod: TokenEndPointAuthMethod,
            maxAgeSinceLastAuthenticated: Long = 300,
            clockSkew: Long = 60) =
            ClientConfiguration
                    .with()
                    .issuer(issuer)
                    .client(CLIENT_ID, CLIENT_REDIRECT_URI, CLIENT_SECRET)
                    .tokenEndPointAuthMethod(tokenEndPointAuthMethod)
                    .maxAgeSinceUserAuthenticated(maxAgeSinceLastAuthenticated)
                    .clockSkewSeconds(clockSkew)


    fun tokenResponse(idToken: String = ID_TOKEN_VALUE,
                      accessToken: String = ACCESS_TOKEN_VALUE,
                      refreshToken: String = REFRESH_TOKEN_VALUE): String {
        return """
    {
      "access_token": "$accessToken",
      "token_type": "Bearer",
      "refresh_token": "$refreshToken",
      "expires_in": 3600,
      "id_token": "$idToken"
    }
        """.trimIndent()
    }
}