package io.github.shyamz.openidconnect.configuration

import io.github.shyamz.openidconnect.TestConstants
import io.github.shyamz.openidconnect.TestConstants.CLIENT_ID
import io.github.shyamz.openidconnect.TestConstants.CLIENT_REDIRECT_URI
import io.github.shyamz.openidconnect.TestConstants.CLIENT_SECRET
import io.github.shyamz.openidconnect.TestConstants.GOOGLE_PROVIDER
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ClientConfigurationTest {

    @Test
    fun `loads all the configuration once`() {

        ClientConfiguration
                .with()
                .issuer("https://accounts.google.com/")
                .client(CLIENT_ID, CLIENT_REDIRECT_URI, CLIENT_SECRET)
                .tokenEndPointAuthMethod(TokenEndPointAuthMethod.Basic)

        assertThat(ClientConfiguration.provider).isEqualTo(GOOGLE_PROVIDER)
        assertThat(ClientConfiguration.client).isEqualTo(TestConstants.OPEN_ID_CLIENT)
        assertThat(ClientConfiguration.tokenEndPointAuthMethod).isEqualTo(TokenEndPointAuthMethod.Basic)
    }
}