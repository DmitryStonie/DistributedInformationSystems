package core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.example.api.requests.CrackHashRequest
import org.example.api.responses.CrackHashResponse
import org.example.api.responses.CrackStatusResponse
import org.example.core.controller.HashCodeCracker
import org.example.core.ui.CrackHashInput
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

class Client(val client: RestClient) {
    fun getStatus(id: String): Deferred<CrackStatusResponse?> = CoroutineScope(Dispatchers.Default).async {
        return@async client.get()
            .uri(HashCodeCracker.MANAGER_STATUS_URI + id)
            .retrieve()
            .body<CrackStatusResponse>()
    }

    fun sendCrackRequest(input: CrackHashInput): Deferred<String?> = CoroutineScope(Dispatchers.Default).async {
        return@async client.post()
            .uri(HashCodeCracker.MANAGER_CRACK_URI)
            .body(CrackHashRequest(input.hash, input.maxLength))
            .retrieve()
            .body<CrackHashResponse>()?.requestId
    }
}