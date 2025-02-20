package org.example.api.client

import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.*
import org.example.api.requests.CrackHashManagerRequest
import org.example.api.requests.TaskStatusWorkerRequest
import org.example.api.responses.CrackHashWorkerResponse
import org.example.api.responses.TaskStatusWorkerResponse
import org.example.core.task.Task
import org.example.core.task.TaskStatus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class Client {
    private val client = RestClient.builder().build()
    private val log = LoggerFactory.getLogger(Client::class.java)


    fun send(){
        val factory = ConnectionFactory()
        factory.host = "localhost"
        factory.newConnection().use { connection -> connection.createChannel().use { channel ->
            channel.queueDeclare(MANAGER_QUEUE_NAME, false, false, false, null)
            val message = "Hello World!"
            channel.basicPublish("", MANAGER_QUEUE_NAME, null, message.toByteArray())
            println(" [x] Sent '$message'")




        } }
    }

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
        const val WORKERS_QUEUE_NAME = "workersQueue"
        const val MANAGER_QUEUE_NAME = "managerQueue"
    }


}