package ru.itmo.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers

class JvmHttpClient : HttpClient {
    private val client = newHttpClient()

    override suspend fun request(method: HttpMethod, request: HttpRequest): HttpResponse {
        var req = newBuilder().uri(URI.create(request.url))
        request.headers.value.forEach { req = req.header(it.key, it.value) }
        val reqType = when (method) {
            HttpMethod.GET -> req
            HttpMethod.POST -> req.POST(BodyPublishers.ofByteArray(request.body))
            HttpMethod.PUT -> req.PUT(BodyPublishers.ofByteArray(request.body))
            HttpMethod.DELETE -> req.DELETE()
        }
        val response = withContext(Dispatchers.IO) {
            client.send(reqType.build(), BodyHandlers.ofString())
        }

        return buildHttpResponse(response)
    }

    private fun buildHttpResponse(response: java.net.http.HttpResponse<String>): HttpResponse {
        val status = HttpStatus(response.statusCode())
        val headers = HttpHeaders(response.headers().map().mapValues { it.value[0] })
        val body = response.body().encodeToByteArray()
        return HttpResponse(status, headers, body)
    }

    override fun close() {
    }

}
