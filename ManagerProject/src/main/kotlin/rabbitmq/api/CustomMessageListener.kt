package org.example.rabbitmq.api

import org.example.core.task.TaskStatus
import org.example.core.task.TaskVault
import org.example.rabbitmq.messages.CrackHashRequest
import org.example.rabbitmq.messages.CrackHashResponse
import org.springframework.stereotype.Service
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Service
class CustomMessageListener(val taskVault: TaskVault, val messageSender: CustomMessageSender) {
    @RabbitListener(queues = arrayOf("workersQueue"))
    fun receiveTaskAccept(message: CrackHashResponse) {
        val task = taskVault.getTask(message.requestId)
        if (task == null) {
            println("task is null id ${message.requestId}")
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
                println("error in task status")
            }
        }
    }
}