package org.example.rabbitmq.api

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.core.HashCodeCracker
import org.example.core.task.Task
import org.example.core.task.TaskStatus
import org.example.core.task.TaskVault
import org.example.rabbitmq.messages.CrackHashRequest
import org.example.rabbitmq.messages.CrackHashResponse
import org.example.rabbitmq.messages.CrackHashStatusRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.amqp.rabbit.annotation.RabbitListener;


@Service
class CustomMessageListener(val sender: CustomMessageSender, val taskVault: TaskVault) {
    private val log = LoggerFactory.getLogger(CustomMessageListener::class.java)

    private final val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        log.info("Exception happened ${throwable.message}")
    }
    val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)

    @RabbitListener(queues = arrayOf("managerQueue"))
    fun receiveTaskRequest(message: CrackHashRequest) {
        println("Received message and deserialized to: ${message}")
        scope.launch {
            launch {
                val cracker = HashCodeCracker()
                val task = Task(cracker, TaskStatus.IN_PROGRESS)
                taskVault.addTask(message.requestId, task)
                task.run(sender, message.requestId, message.hash, message.maxLength, message.numOfWorkers, message.workerNum)
            }
        }
        sender.sendCrackHashResponse(CrackHashResponse(TaskStatus.IN_PROGRESS.value, message.requestId, null))
    }
    @RabbitListener(queues = arrayOf("managerStatusQueue"))
    fun receiveTaskStatus(message: CrackHashStatusRequest) {
        println("Received message and deserialized to: ${message}")
        val task = taskVault.getTask(message.taskId)
        val status = task?.status?.value ?: TaskStatus.ERROR.value
        sender.sendCrackHashResponse(CrackHashResponse(status, message.taskId, null))
    }

}