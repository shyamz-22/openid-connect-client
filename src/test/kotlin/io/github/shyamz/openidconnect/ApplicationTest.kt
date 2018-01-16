package io.github.shyamz.openidconnect

import io.github.shyamz.openidconnect.authorization.OpenIdClient
import io.github.shyamz.openidconnect.discovery.WellKnownConfigDiscoverer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.net.URI

class ApplicationTest {

    @Test
    fun `can write fluently`() {

        val authorizeRequest = WellKnownConfigDiscoverer(URI.create("https://accounts.google.com/"))
                .authorizeRequest(OpenIdClient("id", "http://redirect-uri"))
                .basic()
                .build()

        assertThat(authorizeRequest.authorizeUrl).isNotNull()
    }
}