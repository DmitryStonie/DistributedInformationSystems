package org.example.api.controllers

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.example.api.requests.CrackHashManagerRequest
import org.example.api.requests.CrackHashResultRequest
import org.example.api.responses.CrackHashResponse
import org.example.core.HashCodeCracker
import org.example.core.task.Task
import org.example.core.task.TaskStatus
import org.example.core.task.TaskVault
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@RestController
class Controller {
    private val taskVault = TaskVault()

    @PostMapping("/internal/api/worker/hash/crack/task")
    suspend fun crackHash(@RequestBody request: CrackHashManagerRequest): CrackHashResponse {

        val task = Task(request.requestId, TaskStatus.IN_PROGRESS, ArrayList<String>())
        val result = ArrayList<String>()
        val jobs = ArrayList<Job>()

        coroutineScope {
            launch {
                for(i in 1..request.numOfWorkers){
                    val res = HashCodeCracker().run(request.hash, request.maxLength, request.numOfWorkers, request.workerNum)
                    res?.let { result.addAll(it) }
                }
                val client = RestClient.builder()
                    .baseUrl("https://example.com")
                    .defaultUriVariables(mapOf("variable" to "foo"))
                    .defaultHeader("My-Header", "Foo")
                    .defaultCookie("My-Cookie", "Bar")
                    .build()
                client.patch()
                    .uri("/internal/api/manager/hash/crack/request")
                    .body(CrackHashResultRequest(result))
                    .retrieve()
                    .body<CrackHashResponse>()


            }
        }

        return CrackHashResponse("Ok")
    }


}