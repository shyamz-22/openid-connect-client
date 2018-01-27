package io.github.shyamz.openidconnect.configuration

import io.github.shyamz.openidconnect.TestConstants
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
                .clientId(TestConstants.CLIENT_ID)
                .clientSecret(TestConstants.CLIENT_SECRET)
                .redirectUri(TestConstants.CLIENT_REDIRECT_URI)
                .tokenEndPointAuthMethod(TokenEndPointAuthMethod.Basic)
                .load()

        assertThat(ClientConfiguration.provider).isEqualTo(GOOGLE_PROVIDER)
        assertThat(ClientConfiguration.client).isEqualTo(TestConstants.OPEN_ID_CLIENT)
        assertThat(ClientConfiguration.tokenEndPointAuthMethod).isEqualTo(TokenEndPointAuthMethod.Basic)
    }

}