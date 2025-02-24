package org.example.rabbitmq.api

import kotlinx.coroutines.*
import org.example.core.HashCodeCracker
import org.example.core.task.Task
import org.example.core.task.TaskStatus
import org.example.core.task.TaskVault
import org.example.rabbitmq.messages.CrackHashRequest
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service


@Service
class CustomMessageListener(val sender: CustomMessageSender, val taskVault: TaskVault) {
    private val log = LoggerFactory.getLogger(CustomMessageListener::class.java)

    private final val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        log.info("Exception happened ${throwable.message}")
    }
    val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)

    @RabbitListener(queues = ["managerQueue"])
    fun receiveTaskRequest(
        message: CrackHashRequest
    ) {
        println("Received message and deserialized to: $message")
        val cracker = HashCodeCracker()
        val task = Task(cracker, TaskStatus.IN_PROGRESS)
        taskVault.addTask(message.requestId, task)
        runBlocking { task.run(
            sender,
            message.requestId,
            message.hash,
            message.maxLength,
            message.numOfWorkers,
            message.workerNum
        ).join()
        }
    }


}