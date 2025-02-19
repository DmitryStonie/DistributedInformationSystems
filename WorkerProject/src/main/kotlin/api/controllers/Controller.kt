package org.example.api.controllers

import kotlinx.coroutines.*
import org.example.api.client.Client
import org.example.api.requests.CrackHashManagerRequest
import org.example.api.requests.TaskStatusManagerRequest
import org.example.api.responses.CrackHashManagerResponse
import org.example.api.responses.TaskStatusManagerResponse
import org.example.core.HashCodeCracker
import org.example.core.task.Task
import org.example.core.task.TaskStatus
import org.example.core.task.TaskVault
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
class Controller(private val client: Client, val taskVault: TaskVault) {
    private val log = LoggerFactory.getLogger(Controller::class.java)

    private final val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        log.info("Exception happened ${throwable.message}")
    }
    val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)

    @PostMapping("/internal/api/worker/hash/crack/task")
    suspend fun crackHash(@RequestBody request: CrackHashManagerRequest): CrackHashManagerResponse {
        scope.launch {
            launch {
                val cracker = HashCodeCracker()
                val task = Task(client, cracker, TaskStatus.IN_PROGRESS)
                task.run(request.requestId, request.hash, request.maxLength, request.numOfWorkers, request.workerNum)
                taskVault.addTask(request.requestId, task)
            }
        }
        return CrackHashManagerResponse("Ok")
    }
    @PostMapping("/internal/api/worker/hash/crack/taskStatus")
    suspend fun crackHashStatus(@RequestBody request: TaskStatusManagerRequest): TaskStatusManagerResponse {
        return scope.async {
            TaskStatusManagerResponse(taskVault.getTask(request.taskId)?.status?.value ?: TaskStatus.ERROR.value)
        }.await()
    }
}