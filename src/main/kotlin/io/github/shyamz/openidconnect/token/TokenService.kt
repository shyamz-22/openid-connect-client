package io.github.shyamz.openidconnect.token

import com.mashape.unirest.http.HttpResponse
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
import io.github.shyamz.openidconnect.response.model.Grant
import org.apache.http.HttpHeaders
import org.apache.http.entity.ContentType

class TokenService(private val idProviderConfiguration: IdProviderConfiguration,
                   private val openIdClient: OpenIdClient,
                   private val authMethod: TokenEndPointAuthMethod = Basic) {

    fun exchange(authorizationCodeGrant: AuthorizationCodeGrant): BasicFlowResponse {

        return basicTokenEndpointRequest(authorizationCodeGrant)
                .field("code", authorizationCodeGrant.code)
                .asTokenResponse()
                .handleResponse()
    }

    fun refresh(refreshTokenGrant: RefreshTokenGrant, scope: Set<String> = emptySet()): BasicFlowResponse {

        return basicTokenEndpointRequest(refreshTokenGrant)
                .field("refresh_token", refreshTokenGrant.refreshToken)
                .apply { scope.takeIf { it.isNotEmpty() }?.apply { field("scope", scope.joinToString(" "))} }
                .asTokenResponse()
                .handleResponse()
    }


    private fun basicTokenEndpointRequest(grant: Grant): MultipartBody {
        return UnirestFactory().post(idProviderConfiguration.tokenEndpoint.toString())
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.mimeType)
                .field("grant_type", grant.grantType.grant)
                .field("redirect_uri", openIdClient.redirectUri)
                .addAuthentication()
    }

    private fun MultipartBody.addAuthentication(): MultipartBody {
        return when (authMethod) {
            Basic -> basicAuth(openIdClient.id, openIdClient.secret)
            Post -> postAuth(openIdClient.id, openIdClient.secret)
        }
    }

    private fun HttpResponse<TokenResponse>.handleResponse(): BasicFlowResponse {
        return when (status) {
            200 -> body.toBasicFlowResponse()
            else -> throw OpenIdConnectException("Error while exchanging code for token", body.toErrorResponse())
        }
    }

    private fun MultipartBody.postAuth(id: String, secret: String?): MultipartBody {
        return this.field("client_id", id)
                .field("client_secret", secret)
    }

    private fun MultipartBody.asTokenResponse() = this.asObject(TokenResponse::class.java)
}
