package org.example.api.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.example.api.requests.CrackHashRequest
import org.example.api.responses.CrackHashResponse
import org.example.api.responses.CrackStatusResponse
import org.example.core.ui.CrackHashInput
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
@Component
class Client {
    @Value("\${manager_url}")
    lateinit var MANAGER_URL: String

    private val client: RestClient = RestClient.builder().build()

    fun getStatus(id: String): Deferred<CrackStatusResponse?> = CoroutineScope(Dispatchers.Default).async {
        return@async client.get()
            .uri(MANAGER_URL + "/api/hash/status?requestId=" + id)
            .retrieve()
            .body<CrackStatusResponse>()
    }

    fun sendCrackRequest(input: CrackHashInput): Deferred<String?> = CoroutineScope(Dispatchers.Default).async {
        return@async client.post()
            .uri(MANAGER_URL + "/api/hash/crack")
            .body(CrackHashRequest(input.hash, input.maxLength))
            .retrieve()
            .body<CrackHashResponse>()?.requestId
    }
}