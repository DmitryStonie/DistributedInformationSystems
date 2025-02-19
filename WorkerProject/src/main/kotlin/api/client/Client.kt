package org.example.api.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.example.api.requests.CrackHashResultRequest
import org.example.api.responses.CrackHashResultResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class Client {
    @Value("\${manager_url}")
    lateinit var MANAGER_URL: String

    private val client: RestClient = RestClient.builder().build()

    fun sendResult(requestId: String, result: List<String>?, workerNum: Int): Deferred<CrackHashResultResponse?> = CoroutineScope(Dispatchers.Default).async{
        return@async client.patch()
            .uri(MANAGER_URL + "/internal/api/manager/hash/crack/request")
            .body(CrackHashResultRequest(requestId, result, workerNum))
            .retrieve()
            .body<CrackHashResultResponse>()
    }
}