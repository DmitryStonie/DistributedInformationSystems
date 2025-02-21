package org.example.core.task

import kotlinx.coroutines.*
import org.example.core.HashCodeCracker
import org.example.rabbitmq.api.CustomMessageSender
import org.example.rabbitmq.messages.CrackHashResponse
import org.slf4j.LoggerFactory


class Task(
    val cracker: HashCodeCracker,
    var status: TaskStatus
) {
    private val log = LoggerFactory.getLogger(Task::class.java)
    private final val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        log.info("Exception happened ${throwable.message}")
    }
    val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)

    suspend fun run(
        sender: CustomMessageSender,
        requestId: String,
        hash: String,
        maxLength: Int,
        numOfWorkers: Int,
        workerNum: Int
    ) = scope.launch {
        log.info("Task $requestId in progress")
        status = TaskStatus.IN_PROGRESS
        launch {
            val result = cracker.run(hash, maxLength, numOfWorkers, workerNum).await()
            log.info("Task $requestId counted result ${result}")
            status = TaskStatus.READY
            sender.sendCrackHashResponse(CrackHashResponse(status.value, requestId, result))
        }
        launch {
            while (status == TaskStatus.IN_PROGRESS) {
                sender.sendCrackHashResponse(CrackHashResponse(status.value, requestId, null))
                delay(DELAY)
            }
        }
    }
    companion object {
        const val DELAY = 5000L
    }
}
