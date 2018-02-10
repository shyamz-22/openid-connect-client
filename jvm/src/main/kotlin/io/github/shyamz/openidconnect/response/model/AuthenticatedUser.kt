package io.github.shyamz.openidconnect.response.model

data class AuthenticatedUser(val tokens: BasicFlowResponse,
                             val clientInfo: ClientInfo,
                             val userInfo: UserInfo)

data class ClientInfo(val authorizedClient: String,
                      val interestedClients: List<String> = emptyList())

data class UserInfo(val profile: Profile,
                    val email: Email? = null,
                    val phoneNumber: PhoneNumber? = null,
                    val address: Address? = null)

data class Profile(val userId: String,
                   val name: String? = null,
                   val givenName: String? = null,
                   val familyName: String? = null,
                   val middleName: String? = null,
                   val nickname: String? = null,
                   val preferredUsername: String? = null,
                   val profile: String? = null,
                   val picture: String? = null,
                   val website: String? = null,
                   val gender: String? = null,
                   val birthDate: String? = null,
                   val zoneInfo: String? = null,
                   val locale: String? = null,
                   val updatedAt: Long? = null)

data class Address(val formatted: String? = null,
                   val streetAddress: String? = null,
                   val locality: String? = null,
                   val region: String? = null,
                   val postalCode: String? = null,
                   val country: String? = null)

data class Email(val email: String,
                 val emailVerified: Boolean? = null)

data class PhoneNumber(val phoneNumber: String,
                       val phoneNumberVerified: Boolean? = null)