package io.github.shyamz.openidconnect.mocks

import com.github.tomakehurst.wiremock.client.WireMock.*
import io.github.shyamz.openidconnect.TestConstants
import io.github.shyamz.openidconnect.TestConstants.ERROR_RESPONSE
import io.github.shyamz.openidconnect.TestConstants.SUCCESSFUL_RESPONSE
import org.apache.http.HttpHeaders
import org.apache.http.entity.ContentType

fun stubForTokenResponseWithBasicAuth() {
    stubFor(post(urlPathMatching("/token"))
            .withBasicAuth(TestConstants.CLIENT_ID, TestConstants.CLIENT_SECRET)
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_FORM_URLENCODED.mimeType))
            .willReturn(
                    aResponse()
                            .withStatus(200)
                            .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
                            .withBody(SUCCESSFUL_RESPONSE)))
}

fun stubForTokenResponseWithPostAuth() {
    stubFor(post(urlPathMatching("/token"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_FORM_URLENCODED.mimeType))
            .willReturn(
                    aResponse()
                            .withStatus(200)
                            .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
                            .withBody(SUCCESSFUL_RESPONSE)))
}

fun stubForTokenResponseWithBadRequest() {
    stubFor(post(urlPathMatching("/token"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_FORM_URLENCODED.mimeType))
            .willReturn(
                    aResponse()
                            .withStatus(400)
                            .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
                            .withBody(ERROR_RESPONSE)))
}