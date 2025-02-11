package org.example.api.controllers

import kotlinx.coroutines.*
import org.example.api.requests.CrackHashManagerRequest
import org.example.api.requests.CrackHashResultRequest
import org.example.api.responses.CrackHashWorkerResponse
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
    private val jobs = ArrayList<Job>()

    @PostMapping("/internal/api/worker/hash/crack/task")
    suspend fun crackHash(@RequestBody request: CrackHashManagerRequest): CrackHashWorkerResponse = runBlocking {
        val task = Task(request.requestId, TaskStatus.IN_PROGRESS, ArrayList<String>())
        val result = ArrayList<String>()
        taskVault.addTask(request.requestId, task)
        val job = launch {
            task.run(request.hash, request.maxLength, request.numOfWorkers, request.workerNum)
        }
        jobs.add(job)
        println("send ok")
        CrackHashWorkerResponse("Ok")
    }


}