package org.example.api.controllers

import kotlinx.coroutines.*
import org.example.api.requests.CrackHashManagerRequest
import org.example.api.responses.CrackHashWorkerResponse
import org.example.core.task.Task
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestClient

@RestController
class Controller {
    private val client = RestClient.builder()
        .baseUrl("https://example.com")
        .defaultHeader("My-Header", "Foo")
        .defaultCookie("My-Cookie", "Bar")
        .build()

    @PostMapping("/internal/api/worker/hash/crack/task")
    suspend fun crackHash(@RequestBody request: CrackHashManagerRequest): CrackHashWorkerResponse {
        CoroutineScope(Dispatchers.Default).launch {
            val task = Task(client, request.requestId)
            task.run(request.hash, request.maxLength, request.numOfWorkers, request.workerNum)
        }
        return CrackHashWorkerResponse("Ok")
    }


}