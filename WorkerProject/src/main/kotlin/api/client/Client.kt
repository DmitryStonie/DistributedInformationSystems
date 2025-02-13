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
    @Value("\${my.manager_result}")
    lateinit var managerCrackUri: String

    private val client: RestClient = RestClient.builder().build()

    fun sendResult(requestId: String, result: List<String>?, workerNum: Int): Deferred<CrackHashResultResponse?> = CoroutineScope(Dispatchers.Default).async{
        return@async client.patch()
            .uri(managerCrackUri)
            .body(CrackHashResultRequest(requestId, result, workerNum))
            .retrieve()
            .body<CrackHashResultResponse>()
    }
}