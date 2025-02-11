package org.example.api.controllers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.api.requests.CrackHashClientRequest
import org.example.api.requests.CrackHashManagerRequest
import org.example.api.responses.*
import org.example.core.MessageSender
import org.example.core.task.Task
import org.example.core.task.TaskVault
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.util.concurrent.atomic.AtomicLong

@RestController
class CrackHashController {
    private val client = RestClient.builder()
        .baseUrl("https://example.com")
        .defaultHeader("My-Header", "Foo")
        .defaultCookie("My-Cookie", "Bar")
        .build()
    private val messageSender = MessageSender(client)
    private val counter = AtomicLong()
    private val taskVault = TaskVault()

    @PostMapping("/api/hash/crack")
    fun crackHash(@RequestBody request: CrackHashClientRequest): CrackHashResponse {
        val id = counter.incrementAndGet().toString()
        CoroutineScope(Dispatchers.Default).launch {
            val statuses = ArrayList(generateSequence { "IN_PROGRESS" }.take(1).toList())
            for (i in 1..NUM_OF_WORKERS) {
                val response = messageSender.sendWorkerCrackRequest(id, request.hash, request.maxLength, NUM_OF_WORKERS, i).await()
                if (response?.status == "OK") {
                    println("OK")
                }
            }
            val task = Task(id, statuses, ArrayList<String>())
            println("Added task ${task}")
            if(taskVault.getTask(id) == null){
                taskVault.addTask(id, task)
            }
        }
        return CrackHashResponse(id)
    }

    @GetMapping("/api/hash/status")
    fun getStatus(@RequestParam(value = "requestId") requestId: String?): CrackStatusResponse {
        if (requestId != null && taskVault.getTask(requestId) != null) {
            if (taskVault.getTask(requestId)?.statuses?.contains("IN_PROGRESS") == true) {
                return CrackStatusResponse("IN_PROGRESS", null)
            } else if(taskVault.getTask(requestId)?.statuses?.count{it == "DONE"} == taskVault.getTask(requestId)?.statuses?.count()){
                return CrackStatusResponse("DONE", taskVault.getTask(requestId)?.result)
            } else{
                return CrackStatusResponse("ERROR", null)
            }
        }
        return CrackStatusResponse("NO_SUCH_ID", null)
    }

    @PatchMapping("/internal/api/manager/hash/crack/request")
    fun receiveResult(@RequestBody response: CrackHashResult): CrackHashResultResponse {
        CoroutineScope(Dispatchers.Default).launch {
            var task = taskVault.getTask(response.requestId)
            if (task == null) {
                val statuses = ArrayList(generateSequence { "IN_PROGRESS" }.take(1).toList())
                task = Task(response.requestId, statuses, ArrayList<String>())
                taskVault.addTask(response.requestId, task)
                println("Added task ${task} after worker done job")
            }
            task = taskVault.getTask(response.requestId)
            task?.statuses?.set(0, "DONE")
            if(response.data != null) task?.result?.addAll(response.data)
        }
        return CrackHashResultResponse("OK")
    }

    companion object {
        val WORKER_CRACK_URL = "http://localhost:8082/internal/api/worker/hash/crack/task"
        val NUM_OF_WORKERS = 1
    }

}