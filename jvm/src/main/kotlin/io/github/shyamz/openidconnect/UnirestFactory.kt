package io.github.shyamz.openidconnect

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mashape.unirest.http.ObjectMapper
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.request.GetRequest
import com.mashape.unirest.request.HttpRequestWithBody
import java.io.IOException

internal class UnirestFactory {
    init {
        Unirest.setObjectMapper(AppObjectMapper())
    }

    fun get(url: String): GetRequest {
        return Unirest.get(url)
    }

    fun post(url: String): HttpRequestWithBody {
        return Unirest.post(url)
    }
}

private class AppObjectMapper : ObjectMapper {

    private val objectMapper = jacksonObjectMapper()

    override fun writeValue(value: Any?): String {
        try {
            return objectMapper.writeValueAsString(value)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }

    override fun <T : Any?> readValue(value: String?, valueType: Class<T>?): T {
        try {
            return objectMapper.readValue(value, valueType)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
