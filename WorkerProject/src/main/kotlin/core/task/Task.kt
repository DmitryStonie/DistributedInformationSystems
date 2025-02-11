package org.example.core.task

import kotlinx.coroutines.*
import org.example.api.requests.CrackHashResultRequest
import org.example.api.responses.CrackHashWorkerResponse
import org.example.core.HashCodeCracker
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

class Task(
    val client: RestClient,
    val requestId: String
) {
    suspend fun run(hash: String, maxLength: Int, numOfWorkers: Int, workerNum: Int) = CoroutineScope(Dispatchers.Default).launch {
        val res = HashCodeCracker().run(hash, maxLength, numOfWorkers, workerNum).await()
        println(res)
        var response = sendResult(res).await()
        if (response?.status == "BAD") {
            while (response?.status == "BAD") {
                delay(1000L)
                response = sendResult(res).await()
                if(response != null) println(response) else println("No response")
            }
        }
    }


    fun sendResult(result: List<String>?): Deferred<CrackHashWorkerResponse?> = CoroutineScope(Dispatchers.Default).async{
        return@async client.patch()
            .uri("http://localhost:8080/internal/api/manager/hash/crack/request")
            .body(CrackHashResultRequest(requestId, result))
            .retrieve()
            .body<CrackHashWorkerResponse>()
    }
}