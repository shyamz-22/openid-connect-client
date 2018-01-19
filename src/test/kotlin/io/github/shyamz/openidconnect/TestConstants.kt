package io.github.shyamz.openidconnect

import io.github.shyamz.openidconnect.authorization.request.OpenIdClient
import java.net.URI

object TestConstants {

    const val CLIENT_REDIRECT_URI = "https://openidconnect.net/callback"
    const val CLIENT_ID = "client-id"
    const val CLIENT_SECRET = "client-id-secret"
    const val CLIENT_STATE_VALUE = "randomState"
    const val DIFFERENT_CLIENT_STATE_VALUE = "anotherRandomState"
    const val NONCE_VALUE = "aVeryRandomValue"
    const val AUTH_CODE_VALUE = "SplxlOBeZQQYbYS6WxSbIA"
    const val BLANK = "                           "

    val GOOGLE_ISSUER = URI.create("https://accounts.google.com/")
    val YAHOO_ISSUER = URI.create("https://api.login.yahoo.com")
    val PAYPAL_ISSUER = URI.create("https://www.paypal.com")

    val OPEN_ID_CLIENT = OpenIdClient(CLIENT_ID, CLIENT_REDIRECT_URI, CLIENT_SECRET)


    const val ID_TOKEN_VALUE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ"
    val ACCESS_TOKEN_VALUE = "SlAV32hkKG"
    val REFRESH_TOKEN_VALUE = "8xLOxBtZp8"

    val JSON_RESPONSE = """
{
  "access_token": "$ACCESS_TOKEN_VALUE",
  "token_type": "Bearer",
  "refresh_token": "$REFRESH_TOKEN_VALUE",
  "expires_in": 3600,
  "id_token": "$ID_TOKEN_VALUE"
}
    """.trimIndent()
}