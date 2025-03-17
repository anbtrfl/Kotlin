package ru.itmo.client

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import kotlin.js.Promise

class FetchClient : HttpClient {

    override suspend fun request(method: HttpMethod, request: HttpRequest): HttpResponse {
        val headers = Headers().apply {
            // Добавляем заголовки из request.headers
            request.headers.entries.forEach { (key, value) ->
                append(key, value)
            }
        }

        val response = fetch(
            request.url,
            RequestInit(
                method = method.name,
                body = request.body,
                headers = headers
            )
        ).await()

        return buildHttpResponse(response)
    }



    private suspend fun buildHttpResponse(response: Response): HttpResponse {
        val status = HttpStatus(response.status.toInt())
        val headers = HttpHeaders(buildHeadersMap(response.headers))
        val body = response.text().await().encodeToByteArray()
        return HttpResponse(status, headers, body)
    }

    private fun buildHeadersMap(headers: Headers): Map<String, String> {
        val headersMap = mutableMapOf<String, String>()
        headers.asDynamic().entries().unsafeCast<Iterable<Array<String>>>().forEach { entry ->
            val (key, value) = entry
            headersMap[key] = value
        }
        return headersMap
    }


    override fun close() {
    }

}

fun fetch(url: String, init: RequestInit): Promise<Response> {
    return when (platform) {
        Platform.Node -> {
            nodeFetch(url, init.asNodeOptions())
        }

        Platform.Browser -> {
            window.fetch(url, init)
        }
    }
}

private enum class Platform { Node, Browser }

private val platform: Platform
    get() {
        val hasNodeApi = js(
            """
            (typeof process !== 'undefined' 
                && process.versions != null 
                && process.versions.node != null) ||
            (typeof window !== 'undefined' 
                && typeof window.process !== 'undefined' 
                && window.process.versions != null 
                && window.process.versions.node != null)
            """
        ) as Boolean
        return if (hasNodeApi) Platform.Node else Platform.Browser
    }

private val nodeFetch: dynamic
    get() = js("eval('require')('node-fetch')")

private fun RequestInit.asNodeOptions(): dynamic =
    js("Object").assign(js("Object").create(null), this)
