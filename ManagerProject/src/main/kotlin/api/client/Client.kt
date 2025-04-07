package org.example.api.client

import kotlinx.coroutines.*
import org.example.api.requests.CrackHashManagerRequest
import org.example.api.requests.TaskStatusWorkerRequest
import org.example.api.responses.CrackHashWorkerResponse
import org.example.api.responses.TaskStatusWorkerResponse
import org.example.core.task.TaskStatus
import org.slf4j.LoggerFactory
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body


@Component
class Client {
    private val client = RestClient.builder().requestFactory(getClientHttpRequestFactory()).build()
    private val log = LoggerFactory.getLogger(Client::class.java)

    fun sendWorkerCrackRequest(workerUrl: String, id: String, hash: String, maxLength: Int, numOfWorkers: Int, workerNum: Int): Deferred<CrackHashWorkerResponse?> = CoroutineScope(Dispatchers.Default).async{
        return@async client.post()
            .uri(workerUrl + "/internal/api/worker/hash/crack/task")
            .body(CrackHashManagerRequest(id, hash, maxLength, numOfWorkers, workerNum))
            .retrieve()
            .body<CrackHashWorkerResponse>()
    }
    private fun getTaskStatus(workerUrl: String, id: String): Deferred<TaskStatusWorkerResponse?> = CoroutineScope(Dispatchers.Default).async{
        return@async client.post()
            .uri(workerUrl + "/internal/api/worker/hash/crack/taskStatus")
            .body(TaskStatusWorkerRequest(id))
            .retrieve()
            .body<TaskStatusWorkerResponse>()
    }


    fun checkTaskStatus(workerUrl: String, id: String): Deferred<TaskStatus> = CoroutineScope(Dispatchers.Default).async {
        var result = TaskStatus.ERROR
        while (true) {
            try{
                log.info("task $id on worker $workerUrl try to get}")
                val response = getTaskStatus(workerUrl, id).await()
                log.info("task $id on worker $workerUrl got status ${response?.status}")
                if (response?.status == TaskStatus.READY.value) {
                    result = TaskStatus.READY
                    break
                } else if (response?.status == TaskStatus.IN_PROGRESS.value) {
                    delay(DELAY_TIME)
                }
                else{
                    break
                }
            }
            catch(e: Exception){
                break
            }
        }
        return@async result
    }

    companion object {
        const val DELAY_TIME = 5000L
    }

    private fun getClientHttpRequestFactory(): ClientHttpRequestFactory {
        val clientHttpRequestFactory = HttpComponentsClientHttpRequestFactory()
        clientHttpRequestFactory.setConnectTimeout(10)
        clientHttpRequestFactory.setConnectionRequestTimeout(10)
        return clientHttpRequestFactory
    }


}