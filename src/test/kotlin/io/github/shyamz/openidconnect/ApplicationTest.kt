package io.github.shyamz.openidconnect

import io.github.shyamz.openidconnect.authorization.OpenIdClient
import io.github.shyamz.openidconnect.discovery.WellKnownConfigDiscoverer
import io.github.shyamz.openidconnect.mocks.MockHttpServletResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.net.URI

class ApplicationTest {

    companion object {
        private val AUTHORIZE_URL = "https://accounts.google.com/o/oauth2/v2/auth?client_id=id&redirect_uri=http%3A%2F%2Fredirect-uri&response_type=code&scope=openid"
    }

    @Test
    fun `can build authentication request fluently`() {

        val authorizeRequest = WellKnownConfigDiscoverer(URI.create("https://accounts.google.com/"))
                .authorizeRequest(OpenIdClient("id", "http://redirect-uri"))
                .basic()
                .build()

        assertThat(authorizeRequest.authorizeUrl).isNotNull()
    }

    @Test
    fun `can redirect to authentication request fluently`() {

        val mockHttpServletResponse = MockHttpServletResponse()

        WellKnownConfigDiscoverer(URI.create("https://accounts.google.com/"))
                .authorizeRequest(OpenIdClient("id", "http://redirect-uri"))
                .basic()
                .build()
                .redirect(mockHttpServletResponse)

        assertThat(mockHttpServletResponse.isRedirectedTo(AUTHORIZE_URL)).isTrue()
    }
}