package io.github.shyamz.openidconnect.configuration

import io.github.shyamz.openidconnect.configuration.model.*
import java.net.URI

data class IdProviderConfiguration(val issuer: URI,
                                   val authorizationEndpoint: URI,
                                   val tokenEndpoint: URI,
                                   val jwksEndpoint: URI,
                                   val subjectTypes: List<SubjectType>,
                                   val responseTypes: List<ResponseType>,
                                   val idTokenSigningAlgorithms: List<SigningAlgorithm>,
                                   val scopes: List<String> = emptyList(),
                                   val claims: List<String> = emptyList(),
                                   val grantTypes: List<GrantType> = emptyList(),
                                   val tokenEndpointAuthMethods: List<TokenEndPointAuthMethod> = emptyList(),
                                   val codeChallengeMethods: List<CodeChallengeMethod> = emptyList(),
                                   val userInfoEndPoint: URI? = null,
                                   val revocationEndPoint: URI? = null)