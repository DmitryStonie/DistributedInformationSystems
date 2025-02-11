package org.example.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.example.api.controllers.CrackHashController
import org.example.api.requests.CrackHashManagerRequest
import org.example.api.responses.CrackHashWorkerResponse
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

class MessageSender(val client: RestClient) {

    fun sendWorkerCrackRequest(id: String, hash: String, maxLength: Int, numOfWorkers: Int, workerNum: Int): Deferred<CrackHashWorkerResponse?> = CoroutineScope(Dispatchers.Default).async{
        return@async client.post()
            .uri(CrackHashController.WORKER_CRACK_URL)
            .body(CrackHashManagerRequest(id, hash, maxLength, numOfWorkers, workerNum))
            .retrieve()
            .body<CrackHashWorkerResponse>()
    }


}