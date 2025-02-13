package org.example.api.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.example.api.requests.CrackHashManagerRequest
import org.example.api.responses.CrackHashWorkerResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class Client {
    @Value("\${my.worker_crack_url}")
    lateinit var workerCrackUrl: String

    private val client = RestClient.builder().build()

    fun sendWorkerCrackRequest(id: String, hash: String, maxLength: Int, numOfWorkers: Int, workerNum: Int): Deferred<CrackHashWorkerResponse?> = CoroutineScope(Dispatchers.Default).async{
        return@async client.post()
            .uri(workerCrackUrl)
            .body(CrackHashManagerRequest(id, hash, maxLength, numOfWorkers, workerNum))
            .retrieve()
            .body<CrackHashWorkerResponse>()
    }


}