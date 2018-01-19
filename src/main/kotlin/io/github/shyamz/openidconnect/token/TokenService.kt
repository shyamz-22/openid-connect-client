package io.github.shyamz.openidconnect.token

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mashape.unirest.http.ObjectMapper
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.request.body.MultipartBody
import io.github.shyamz.openidconnect.authorization.request.OpenIdClient
import io.github.shyamz.openidconnect.authorization.response.model.AuthorizationCodeGrant
import io.github.shyamz.openidconnect.configuration.IdProviderConfiguration
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod.Basic
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod.Post
import org.apache.http.HttpHeaders
import org.apache.http.entity.ContentType
import java.io.IOException

class TokenService(private val idProviderConfiguration: IdProviderConfiguration,
                   private val openIdClient: OpenIdClient,
                   private val authMethod: TokenEndPointAuthMethod = Basic) {

    init {
        Unirest.setObjectMapper(object : ObjectMapper {
            private val objectMapper = jacksonObjectMapper()

            override fun <T> readValue(value: String, valueType: Class<T>): T {
                try {
                    return objectMapper.readValue(value, valueType)
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }

            }

            override fun writeValue(value: Any): String {
                try {
                    return objectMapper.writeValueAsString(value)
                } catch (e: JsonProcessingException) {
                    throw RuntimeException(e)
                }

            }
        })
    }

    fun exchange(authorizationCodeGrant: AuthorizationCodeGrant): BasicFlowResponse {

        return Unirest.post(idProviderConfiguration.tokenEndpoint.toString())
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.mimeType)
                .field("grant_type", authorizationCodeGrant.grantType)
                .field("code", authorizationCodeGrant.code)
                .field("redirect_uri", openIdClient.redirectUri)
                .addAuthentication()
                .asObject(TokenResponse::class.java)
                .body
                .toBasicFlowResponse()
    }

    private fun MultipartBody.addAuthentication(): MultipartBody {
        return when (authMethod) {
            Basic -> basicAuth(openIdClient.id, openIdClient.secret)
            Post -> postAuth(openIdClient.id, openIdClient.secret)
        }
    }

    private fun MultipartBody.postAuth(id: String, secret: String?): MultipartBody {
        return this.field("client_id", id)
                .field("client_secret", secret)
    }

}
