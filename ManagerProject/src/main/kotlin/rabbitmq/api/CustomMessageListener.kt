package org.example.rabbitmq.api

import com.rabbitmq.client.Channel
import org.example.core.task.TaskStatus
import org.example.core.task.TaskVault
import org.example.rabbitmq.messages.CrackHashResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.messaging.handler.annotation.Header

@Service
class CustomMessageListener(val taskVault: TaskVault) {
    private val log = LoggerFactory.getLogger(CustomMessageSender::class.java)

    @RabbitListener(queues = ["workersQueue"])
    fun receiveTaskAccept(message: CrackHashResponse) {
        val task = taskVault.getTask(message.requestId)
        if (task == null) {
            log.info("task is null id ${message.requestId}")
            return
        }
        println("${task.requestId}  ${task.id} ${message.status}")
        when (message.status) {
            TaskStatus.IN_PROGRESS.value -> {
                task.status = TaskStatus.IN_PROGRESS
                task.isDied = false
                taskVault.saveTask(task)
            }

            TaskStatus.ERROR.value -> {
                task.status = TaskStatus.ERROR
                task.isDied = true
                taskVault.saveTask(task)
            }

            TaskStatus.READY.value -> {
                task.status = TaskStatus.READY
                task.isDied = false
                if (message.data != null) {
                    if (task.result == null) {
                        task.result = ArrayList()
                    }
                    task.result!!.addAll(message.data)
                }
                taskVault.saveTask(task)
            }

            else -> {
                log.info("error in task status")
            }
        }
    }
}