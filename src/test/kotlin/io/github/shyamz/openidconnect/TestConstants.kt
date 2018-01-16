package io.github.shyamz.openidconnect

import java.net.URI

object TestConstants {

    val CLIENT_REDIRECT_URI = "https://openidconnect.net/callback"
    val CLIENT_ID = "client-id"
    val CLIENT_STATE_VALUE = "randomState"
    val NONCE_VALUE = "aVeryRandomValue"
    val ID_TOKEN_VALUE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ"

    val GOOGLE_ISSUER = URI.create("https://accounts.google.com/")
    val YAHOO_ISSUER = URI.create("https://api.login.yahoo.com")
    val PAYPAL_ISSUER = URI.create("https://www.paypal.com")

}