package io.github.shyamz.openidconnect.discovery

import io.github.shyamz.openidconnect.configuration.IdProviderConfiguration
import io.github.shyamz.openidconnect.configuration.model.*
import java.net.URI

internal data class ProviderConfigurationModel(private val issuer: URI,
                                               private val authorization_endpoint: URI,
                                               private val token_endpoint: URI,
                                               private val jwks_uri: URI,
                                               private val response_types_supported: List<String>,
                                               private val id_token_signing_alg_values_supported: List<String>,
                                               private val subject_types_supported: List<String>,
                                               private val scopes_supported: List<String> = emptyList(),
                                               private val token_endpoint_auth_methods_supported: List<String> = emptyList(),
                                               private val claims_supported: List<String> = emptyList(),
                                               private val grant_types_supported: List<String> = emptyList(),
                                               private val code_challenge_methods_supported: List<String> = emptyList(),
                                               private val response_modes_supported: List<String> = emptyList(),
                                               private val userinfo_endpoint: URI? = null,
                                               private val revocation_endpoint: URI? = null,
                                               private val token_revocation_endpoint: URI? = null) {

    private fun responseTypes(): List<ResponseType> {

        return response_types_supported
                .map {
                    ResponseType.values().find { type -> type.parameter == it }
                            ?: throw RuntimeException("unsupported response types: $response_types_supported")
                }
    }

    private fun signingAlgorithms(): List<SigningAlgorithm> {

        return id_token_signing_alg_values_supported
                .map {
                    SigningAlgorithm.values().find { signingAlgorithm ->
                        signingAlgorithm.name == it
                    } ?: throw RuntimeException("unsupported signing algorithm: $id_token_signing_alg_values_supported")
                }
    }

    private fun authMethods(): List<TokenEndPointAuthMethodSupported> {

        return token_endpoint_auth_methods_supported
                .map {
                    TokenEndPointAuthMethodSupported.values().find { authMethod ->
                        authMethod.supportedMethod == it
                    } ?: throw RuntimeException("unsupported auth method: $token_endpoint_auth_methods_supported")
                }
    }

    private fun codeChallengeMethods(): List<CodeChallengeMethod> {

        return code_challenge_methods_supported
                .map {
                    CodeChallengeMethod.values().find { codeChallengeMethod ->
                        codeChallengeMethod.method == it
                    } ?: throw RuntimeException("unsupported code challenge method: $code_challenge_methods_supported")
                }
    }

    private fun grantTypes(): List<GrantType> {

        return grant_types_supported
                .map {
                    GrantType.values().find { type ->
                        type.grant == it
                    } ?: throw RuntimeException("unsupported grant types: $grant_types_supported")
                }
    }

    private fun subjectTypes(): List<SubjectType> {

        return subject_types_supported
                .map {
                    SubjectType.values().find { subject ->
                        subject.type == it
                    } ?: throw RuntimeException("unsupported subject types: $subject_types_supported")
                }
    }


    private fun tokenRevocationEndPoint(): URI? {
        return token_revocation_endpoint ?: revocation_endpoint
    }

    fun idProviderConfig(): IdProviderConfiguration {
        return IdProviderConfiguration(issuer,
                authorization_endpoint,
                token_endpoint,
                jwks_uri,
                subjectTypes(),
                responseTypes(),
                signingAlgorithms(),
                scopes_supported,
                claims_supported,
                grantTypes(),
                authMethods(),
                codeChallengeMethods(),
                userinfo_endpoint,
                tokenRevocationEndPoint())
    }
}


