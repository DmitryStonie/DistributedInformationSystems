package org.example.api.controllers

import kotlinx.coroutines.*
import org.example.api.requests.CrackHashManagerRequest
import org.example.api.responses.CrackHashManagerResponse
import org.example.core.task.Task
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
class Controller(private val task: Task) {
    private val log = LoggerFactory.getLogger(Controller::class.java)

    private final val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        log.info("Exception happened ${throwable.message}")
    }
    val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)

    @PostMapping("/internal/api/worker/hash/crack/task")
    suspend fun crackHash(@RequestBody request: CrackHashManagerRequest): CrackHashManagerResponse {
        scope.launch {
            launch {
                task.run(request.requestId, request.hash, request.maxLength, request.numOfWorkers, request.workerNum)
            }
        }
        return CrackHashManagerResponse("Ok")
    }
}