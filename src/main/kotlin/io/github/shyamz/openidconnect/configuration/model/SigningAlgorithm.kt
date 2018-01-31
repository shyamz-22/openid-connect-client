package io.github.shyamz.openidconnect.configuration.model

import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException

enum class SigningAlgorithm {
    RS256,
    ES256,
    HS256;

    fun algorithmType(): AlgorithmType {
        return AlgorithmType.typeOf(this)
    }
}

enum class AlgorithmType(val type: String) {

    RSA("RS"),
    ECDSA("ES"),
    HMAC("HS");

    companion object {
        fun typeOf(algorithm: SigningAlgorithm): AlgorithmType {
            return values().firstOrNull { algorithm.name.startsWith(it.type) }
                    ?: throw OpenIdConnectException("No supported algorithm type found for signing algorithm '$algorithm'")
        }
    }
}