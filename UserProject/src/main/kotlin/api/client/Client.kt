package org.example.api.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.example.api.requests.CrackHashRequest
import org.example.api.responses.CrackHashResponse
import org.example.api.responses.CrackStatusResponse
import org.example.core.ui.CrackHashInput
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

class Client(private val client: RestClient) {
    fun getStatus(id: String): Deferred<CrackStatusResponse?> = CoroutineScope(Dispatchers.Default).async {
        return@async client.get()
            .uri(MANAGER_STATUS_URI + id)
            .retrieve()
            .body<CrackStatusResponse>()
    }

    fun sendCrackRequest(input: CrackHashInput): Deferred<String?> = CoroutineScope(Dispatchers.Default).async {
        return@async client.post()
            .uri(MANAGER_CRACK_URI)
            .body(CrackHashRequest(input.hash, input.maxLength))
            .retrieve()
            .body<CrackHashResponse>()?.requestId
    }
    companion object {
        const val MANAGER_CRACK_URI: String = "http://localhost:8080/api/hash/crack"
        const val MANAGER_STATUS_URI: String = "http://localhost:8080/api/hash/status?requestId="
    }
}