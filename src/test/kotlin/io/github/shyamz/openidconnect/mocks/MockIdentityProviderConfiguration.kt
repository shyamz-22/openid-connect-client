package io.github.shyamz.openidconnect.mocks

import io.github.shyamz.openidconnect.configuration.IdProviderConfiguration
import io.github.shyamz.openidconnect.configuration.model.ResponseType
import io.github.shyamz.openidconnect.configuration.model.SigningAlgorithm
import io.github.shyamz.openidconnect.configuration.model.SubjectType
import java.net.URI

class MockIdentityProviderConfiguration {

    companion object {

        fun get() = IdProviderConfiguration(issuer = URI.create("http://localhost:8089"),
                authorizationEndpoint = URI.create("http://localhost:8089/authorize"),
                tokenEndpoint = URI.create("http://localhost:8089/token"),
                jwksEndpoint = URI.create("http://localhost:8089/keys"),
                subjectTypes = listOf(SubjectType.Public),
                responseTypes = listOf(ResponseType.Code),
                idTokenSigningAlgorithms = listOf(SigningAlgorithm.RS256))

    }

}