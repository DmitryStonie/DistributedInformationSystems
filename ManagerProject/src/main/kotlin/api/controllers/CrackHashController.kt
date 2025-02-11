package org.example.api.controllers

import org.example.api.requests.CrackHashClientRequest
import org.example.api.requests.CrackHashManagerRequest
import org.example.api.responses.*
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
            .baseUrl("https://example2.com")
            .defaultUriVariables(mapOf("variable" to "foo"))
            .defaultHeader("My-Header", "Foo")
            .defaultCookie("My-Cookie", "Bar")
            .build()


        val statuses = ArrayList<String>()
        for (i in 1..numOfWorkers) {
            statuses.add("IN_PROGRESS")
            val response = client.post()
                .uri(WORKER_CRACK_URL)
                .body(CrackHashManagerRequest(id, request.hash, request.maxLength, 1, 1))
                .retrieve()
                .body<CrackHashWorkerResponse>()
            if (response?.status == "OK") {
                println("OK")
            }
        }
        val task = Task(id, statuses, ArrayList<String>())
        println("Added task ${task} after worker ok")
        if(taskVault.getTask(id) == null){
            taskVault.addTask(id, task)
        }

        return CrackHashResponse(id)
    }

    @GetMapping("/api/hash/status")
    fun getStatus(@RequestParam(value = "requestId") requestId: String?): CrackStatusResponse {
        if (requestId != null) {
            if (taskVault.getTask(requestId)?.statuses?.contains("IN_PROGRESS") == true) {
                return CrackStatusResponse("IN_PROGRESS", null)
            } else {
                //if 1 error?
                return CrackStatusResponse("DONE", taskVault.getTask(requestId)?.result)
            }
        } else {
            //error
            return CrackStatusResponse("IN_PROGRESS", null)
        }
    }

    @PatchMapping("/internal/api/manager/hash/crack/request")
    fun receiveResult(@RequestBody response: CrackHashResult): CrackHashResultResponse {
        var task = taskVault.getTask(response.requestId)
        if (task == null) {
            val statuses = ArrayList(generateSequence { "IN_PROGRESS" }.take(1).toList())
            task = Task(response.requestId, statuses, ArrayList<String>())
            taskVault.addTask(response.requestId, task)
            println("Added task ${task} after worker done job")
        }
        task = taskVault.getTask(response.requestId)
        task?.statuses?.set(0, "DONE")
        task?.result?.addAll(response.data)
        return CrackHashResultResponse("OK")
    }

    companion object {
        val WORKER_CRACK_URL = "http://localhost:8082/internal/api/worker/hash/crack/task"
    }

}