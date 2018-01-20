package io.github.shyamz.openidconnect.token

import com.mashape.unirest.request.body.MultipartBody
import io.github.shyamz.openidconnect.UnirestFactory
import io.github.shyamz.openidconnect.authorization.request.AuthorizationCodeGrant
import io.github.shyamz.openidconnect.authorization.request.OpenIdClient
import io.github.shyamz.openidconnect.configuration.IdProviderConfiguration
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod.Basic
import io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod.Post
import io.github.shyamz.openidconnect.exceptions.OpenIdConnectException
import io.github.shyamz.openidconnect.response.model.BasicFlowResponse
import org.apache.http.HttpHeaders
import org.apache.http.entity.ContentType

class TokenService(private val idProviderConfiguration: IdProviderConfiguration,
                   private val openIdClient: OpenIdClient,
                   private val authMethod: TokenEndPointAuthMethod = Basic) {

    fun exchange(authorizationCodeGrant: AuthorizationCodeGrant): BasicFlowResponse {

        val result = UnirestFactory().post(idProviderConfiguration.tokenEndpoint.toString())
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.mimeType)
                .field("grant_type", authorizationCodeGrant.grantType.grant)
                .field("code", authorizationCodeGrant.code)
                .field("redirect_uri", openIdClient.redirectUri)
                .addAuthentication()
                .asObject(TokenResponse::class.java)

        return when(result.status) {
            200 -> result.body.toBasicFlowResponse()
            else -> throw OpenIdConnectException("Error while exchanging code for token", result.body.toErrorResponse())
        }
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
