package io.github.shyamz.openidconnect.mocks

import com.github.tomakehurst.wiremock.client.WireMock.*
import io.github.shyamz.openidconnect.TestConstants
import io.github.shyamz.openidconnect.TestConstants.CLIENT_ID
import io.github.shyamz.openidconnect.TestConstants.CLIENT_SECRET
import org.apache.http.HttpHeaders
import org.apache.http.entity.ContentType

fun stubForTokenResponseWithBasicAuth(expectedResponse: String) {
    stubFor(post(urlPathMatching("/token"))
            .withBasicAuth(CLIENT_ID, CLIENT_SECRET)
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_FORM_URLENCODED.mimeType))
            .willReturn(
                    aResponse()
                            .withStatus(200)
                            .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
                            .withBody(expectedResponse)))
}

fun stubForTokenResponseWithPostAuth(expectedResponse: String) {
    stubFor(post(urlPathMatching("/token"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_FORM_URLENCODED.mimeType))
            .willReturn(
                    aResponse()
                            .withStatus(200)
                            .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
                            .withBody(expectedResponse)))
}

fun stubForTokenResponseWithBadRequest(expectedErrorResponse: String) {
    stubFor(post(urlPathMatching("/token"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_FORM_URLENCODED.mimeType))
            .withBasicAuth(CLIENT_ID, CLIENT_SECRET)
            .willReturn(
                    aResponse()
                            .withStatus(400)
                            .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
                            .withBody(expectedErrorResponse)))
}