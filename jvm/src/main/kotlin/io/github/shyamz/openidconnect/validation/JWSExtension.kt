package io.github.shyamz.openidconnect.validation

import com.nimbusds.jose.JWSHeader
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jwt.util.DateUtils
import io.github.shyamz.openidconnect.StandardClaims.address
import io.github.shyamz.openidconnect.StandardClaims.birthdate
import io.github.shyamz.openidconnect.StandardClaims.country
import io.github.shyamz.openidconnect.StandardClaims.email
import io.github.shyamz.openidconnect.StandardClaims.email_verified
import io.github.shyamz.openidconnect.StandardClaims.family_name
import io.github.shyamz.openidconnect.StandardClaims.formatted
import io.github.shyamz.openidconnect.StandardClaims.gender
import io.github.shyamz.openidconnect.StandardClaims.given_name
import io.github.shyamz.openidconnect.StandardClaims.locale
import io.github.shyamz.openidconnect.StandardClaims.locality
import io.github.shyamz.openidconnect.StandardClaims.middle_name
import io.github.shyamz.openidconnect.StandardClaims.name
import io.github.shyamz.openidconnect.StandardClaims.nickname
import io.github.shyamz.openidconnect.StandardClaims.phone_number
import io.github.shyamz.openidconnect.StandardClaims.phone_number_verified
import io.github.shyamz.openidconnect.StandardClaims.picture
import io.github.shyamz.openidconnect.StandardClaims.postal_code
import io.github.shyamz.openidconnect.StandardClaims.preferred_username
import io.github.shyamz.openidconnect.StandardClaims.profile
import io.github.shyamz.openidconnect.StandardClaims.region
import io.github.shyamz.openidconnect.StandardClaims.street_address
import io.github.shyamz.openidconnect.StandardClaims.updated_at
import io.github.shyamz.openidconnect.StandardClaims.website
import io.github.shyamz.openidconnect.StandardClaims.zoneinfo
import io.github.shyamz.openidconnect.configuration.ClientConfiguration
import io.github.shyamz.openidconnect.configuration.model.AlgorithmType
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import io.github.shyamz.openidconnect.response.model.*
import java.time.Duration
import java.time.Instant
import java.util.*

internal fun JWSHeader.signingAlgorithm(): AlgorithmType {
    return ClientConfiguration.provider.idTokenSigningAlgorithms.firstOrNull {
        it.name == algorithm.name
    }?.algorithmType() ?: throw OpenIdConnectException("Malicious Token. IdP does not support Algorithm '$algorithm'")
}

internal fun JWTClaimsSet.validateTimeElapsedSinceLastAuthentication(): JWTClaimsSet {

    getDateClaim("auth_time")?.let {
        val elapsedTimeSinceLastAuthenticated = Duration.between(Instant.ofEpochMilli(it.time), Instant.now())
        if (elapsedTimeSinceLastAuthenticated.abs().seconds > ClientConfiguration.maxAgeSinceUserAuthenticated) {
            throw OpenIdConnectException("User last authenticated was '${elapsedTimeSinceLastAuthenticated.abs().toMinutes()}' minutes before. Re authenticate the user")
        }
    }

    return this
}

internal fun JWTClaimsSet.validateNonce(nonce: String? = null): JWTClaimsSet {
    if (getStringClaim("nonce") != nonce) {
        throw OpenIdConnectException("Expected nonce '${getStringClaim("nonce")}' in id_token to match stored nonce '$nonce'")
    }
    return this
}

internal fun JWTClaimsSet.validateIssuedAt(): JWTClaimsSet {
    val issuedDuration = Duration.between(Instant.now(), Instant.ofEpochMilli(issueTime.time))
    if (issuedDuration.abs().seconds > ClientConfiguration.clockSkewSeconds) {
        throw OpenIdConnectException("id_token is issued at '$issueTime' is too far away from the current time '${Date()}'")
    }
    return this
}

internal fun JWTClaimsSet.validateTokenExpiry(): JWTClaimsSet {
    if (DateUtils.isAfter(Date(), expirationTime, ClientConfiguration.clockSkewSeconds)) {
        throw OpenIdConnectException("id_token expired. Token expiration time is '$expirationTime'")
    }
    return this
}

internal fun JWTClaimsSet.validateAuthorizedParty(): JWTClaimsSet {
    if (getStringClaim("azp").isNullOrBlank().not() && getStringClaim("azp") != ClientConfiguration.client.id) {
        throw OpenIdConnectException("Expected azp '${getStringClaim("azp")}' in id_token to match client id '${ClientConfiguration.client.id}'")
    }
    return this
}

internal fun JWTClaimsSet.validateAudienceHasAuthorizedParty(): JWTClaimsSet {
    if (audience.size > 1 && getStringClaim("azp").isNullOrBlank()) {
        throw OpenIdConnectException("Expected id_token with multiple audiences '$audience' to have an 'azp' claim. But found none")
    }
    return this
}

internal fun JWTClaimsSet.validateAudience(): JWTClaimsSet {
    if (audience.contains(ClientConfiguration.client.id).not()) {
        throw OpenIdConnectException("Expected audience '$audience' in id_token to contain client '${ClientConfiguration.client.id}'")
    }

    return this
}

internal fun JWTClaimsSet.validateIssuer(): JWTClaimsSet {
    if (issuer != ClientConfiguration.provider.issuer.toString()) {
        throw OpenIdConnectException("Expected issuer '$issuer' in id_token to match well known config issuer '${ClientConfiguration.provider.issuer}'")
    }

    return this
}

internal fun SignedJWT.verifySignature(): JWTClaimsSet {
    if (!verify(SignatureVerifierFactory.jwsVerifier(this.header))) {
        throw OpenIdConnectException("Malicious Token. signature verification failed for token: \n'${this.serialize()}'")
    }
    return this.jwtClaimsSet
}

internal fun JWTClaimsSet.clientInfo(): ClientInfo {
    return audience
            .takeIf { it.size > 1 }
            ?.let {
                ClientInfo(authorizedParty(), audience.filterNot { it == authorizedParty() })
            } ?: ClientInfo(audience.first())
}

internal fun JWTClaimsSet.authorizedParty() = getStringClaim("azp")

internal fun JWTClaimsSet.userInfo(): UserInfo {

    val profile = Profile(
            userId = subject,
            name = getStringClaim(name),
            givenName = getStringClaim(given_name),
            familyName = getStringClaim(family_name),
            middleName = getStringClaim(middle_name),
            nickname = getStringClaim(nickname),
            preferredUsername = getStringClaim(preferred_username),
            profile = getStringClaim(profile),
            picture = getStringClaim(picture),
            website = getStringClaim(website),
            gender = getStringClaim(gender),
            birthDate = getStringClaim(birthdate),
            locale = getStringClaim(locale),
            zoneInfo = getStringClaim(zoneinfo),
            updatedAt = getLongClaim(updated_at)
    )

    val email = getStringClaim(email)
            ?.takeUnless { it.isBlank() }
            ?.let {
                Email(email = it,
                        emailVerified = getBooleanClaim(email_verified))
            }

    val phone = getStringClaim(phone_number)
            ?.takeUnless { it.isBlank() }
            ?.let {
                PhoneNumber(phoneNumber = it,
                        phoneNumberVerified = getBooleanClaim(phone_number_verified))
            }

    val address = getJSONObjectClaim(address)
            ?.let {
                Address(formatted = it.getAsString(formatted),
                        streetAddress = it.getAsString(street_address),
                        locality = it.getAsString(locality),
                        region = it.getAsString(region),
                        postalCode = it.getAsString(postal_code),
                        country = it.getAsString(country)
                )
            }


    return UserInfo(profile, email, phone, address)
}
