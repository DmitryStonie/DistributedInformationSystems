package org.example.core.task

import kotlinx.coroutines.*
import org.example.api.client.Client
import org.example.api.responses.CrackHashResultResponse
import org.example.core.HashCodeCracker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class Task(
    val client: Client,
    val cracker: HashCodeCracker
) {
    private val log = LoggerFactory.getLogger(Task::class.java)
    private final val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        log.info("Exception happened ${throwable.message}")
    }
    val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)

    suspend fun run(requestId: String, hash: String, maxLength: Int, numOfWorkers: Int, workerNum: Int) = scope.launch {
        launch {
            val res = cracker.run(hash, maxLength, numOfWorkers, workerNum).await()
            log.info("Task $requestId counted result $res")
            var response = client.sendResult(requestId, res, workerNum).await()
            if(response?.status == CrackHashResultResponse.Companion.Status.OK){
                log.info("Task $requestId result delivered")
            }else if (response?.status == CrackHashResultResponse.Companion.Status.BAD) {
                while (response?.status == CrackHashResultResponse.Companion.Status.BAD) {
                    log.info("Task got bad request while sending result")
                    delay(DELAY)
                    response = client.sendResult(requestId, res, workerNum).await()
                }
            }
        }
    }

    companion object{
        const val DELAY = 1000L
    }

}