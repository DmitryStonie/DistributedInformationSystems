package org.example.api.controllers

import kotlinx.coroutines.Job
import org.example.api.requests.CrackHashClientRequest
import org.example.api.requests.CrackHashWorkerRequest
import org.example.api.responses.CrackHashResponse
import org.example.api.responses.CrackHashResult
import org.example.api.responses.CrackHashResultResponse
import org.example.api.responses.CrackStatusResponse
import org.example.core.task.Task
import org.example.core.task.TaskVault
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.util.concurrent.atomic.AtomicLong

@RestController
class CrackHashController {
    private val counter = AtomicLong()
    private val taskVault = TaskVault()

    @PostMapping("/api/hash/crack")
    fun crackHash(@RequestBody request: CrackHashClientRequest): CrackHashResponse {
        val id = counter.incrementAndGet().toString()
        val numOfWorkers = 1
        val client = RestClient.builder()
            .baseUrl("https://example.com")
            .defaultUriVariables(mapOf("variable" to "foo"))
            .defaultHeader("My-Header", "Foo")
            .defaultCookie("My-Cookie", "Bar")
            .build()


        val statuses = ArrayList<String>()
        for(i in 1..numOfWorkers){
            statuses.add("IN_PROGRESS")
            client.post()
                .uri(WORKER_CRACK_URL)
                .body(CrackHashWorkerRequest(id, request.hash, request.maxLength,1, 1))
                .retrieve()
                .body<CrackHashResponse>()
        }
        taskVault.addTask(id, Task(id, statuses, ArrayList<String>()))

        return CrackHashResponse(id)
    }

    @GetMapping("/api/hash/status")
    fun getStatus(@RequestParam(value = "requestId") requestId: String?): CrackStatusResponse {
        if(requestId != null){
            if(taskVault.getTask(requestId)?.statuses?.contains("IN_PROGRESS") == true){
                CrackStatusResponse("IN_PROGRESS", null)
            } else{
                //if 1 error?
                CrackStatusResponse("DONE", taskVault.getTask(requestId)?.result)
            }
        } else{
            //error
            CrackStatusResponse("IN_PROGRESS", null)
        }
        return CrackStatusResponse("IN_PROGRESS", arrayListOf("abcd"))
    }

    @PatchMapping("/internal/api/manager/hash/crack/request")
    fun receiveResult(@RequestBody response: CrackHashResult): CrackHashResultResponse {
        val task =  taskVault.getTask(response.requestId)
        task?.statuses?.set(0, "DONE")
        task?.result?.addAll(response.data)
        println(response.data)
        return CrackHashResultResponse("OK")
    }

    companion object{
        val WORKER_CRACK_URL = "http://localhost:8082/internal/api/worker/hash/crack/task"
    }

}