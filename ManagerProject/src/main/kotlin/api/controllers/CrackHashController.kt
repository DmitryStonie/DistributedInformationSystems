package org.example.api.controllers

import kotlinx.coroutines.*
import org.example.core.task.TaskStatus
import org.example.api.client.Client
import org.example.api.requests.CrackHashClientRequest
import org.example.api.responses.*
import org.example.core.task.Task
import org.example.core.task.TaskVault
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList

@RestController
class CrackHashController(val client: Client, val taskVault: TaskVault) {
    @Value("\${my.num_of_workers}")
    lateinit var numberOfWorkers: String

    private val log = LoggerFactory.getLogger(CrackHashController::class.java)

    private final val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        log.info("Exception happened ${throwable.message}")
    }
    val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)
    val taskVaultContext = newSingleThreadContext("taskVaultContext")


    @PostMapping("/api/hash/crack")
    fun crackHash(@RequestBody request: CrackHashClientRequest): CrackHashResponse {
        val id = UUID.randomUUID().toString()
        scope.launch {
            launch {
                for (i in 1..numberOfWorkers.toInt()) {
                    client.sendWorkerCrackRequest(id, request.hash, request.maxLength, numberOfWorkers.toInt(), i)
                        .await()
                }
                withContext(taskVaultContext) {
                    taskVault.createTask(id, numberOfWorkers)
                }
                log.info("Added task with id $id")
            }
        }
        return CrackHashResponse(id)
    }

    @GetMapping("/api/hash/status")
    fun getStatus(@RequestParam(value = "requestId") requestId: String?): CrackStatusResponse {
        var response = CrackStatusResponse(TaskStatus.ERROR, null)
        scope.launch {
            launch(taskVaultContext) {
                if (requestId != null && taskVault.getTask(requestId) != null) {
                    response =
                        if (taskVault.getTask(requestId)?.statuses?.contains(TaskStatus.IN_PROGRESS) == true) {
                            CrackStatusResponse(TaskStatus.IN_PROGRESS, null)
                        } else if (taskVault.getTask(requestId)?.statuses?.count { it == TaskStatus.DONE } == taskVault.getTask(
                                requestId
                            )?.statuses?.count()) {
                            CrackStatusResponse(TaskStatus.DONE, taskVault.getTask(requestId)?.result)
                        } else {
                            CrackStatusResponse(TaskStatus.ERROR, null)
                        }
                }
            }
        }
        return response
    }

    @PatchMapping("/internal/api/manager/hash/crack/request")
    fun receiveResult(@RequestBody response: CrackHashResult): CrackHashResultResponse {
        scope.launch {
            launch(taskVaultContext) {
                var task = taskVault.getTask(response.requestId)
                if (task == null) {
                    task = taskVault.createTask(response.requestId, numberOfWorkers)
                }
                task.statuses[response.workerNum - 1] = TaskStatus.DONE
                if (response.data != null) task.result.addAll(response.data)
            }
        }
        return CrackHashResultResponse(CrackHashResultResponse.Companion.Status.OK)
    }

}