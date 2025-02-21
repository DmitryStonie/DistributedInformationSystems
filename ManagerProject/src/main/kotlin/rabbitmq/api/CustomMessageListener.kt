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
        val subtask = taskVault.getSubtask(message.requestId)
        if(subtask == null){
            println("subtask is null")
            return
        }
        println("${subtask.taskId}  ${subtask.id} ${subtask.status}")
        when (message.status) {
            TaskStatus.IN_PROGRESS.value -> {
                subtask.status = TaskStatus.IN_PROGRESS
            }

            TaskStatus.ERROR.value -> {
                subtask.status = TaskStatus.CREATED
                val newWorkerId = (1..subtask.numOfWorkers).random()
                val newMessage =
                    CrackHashRequest(subtask.id.toString(), subtask.hash, subtask.maxLength, subtask.numOfWorkers, newWorkerId)
                messageSender.sendCrackHashRequest(newMessage)
            }

            TaskStatus.READY.value -> {
                subtask.status = TaskStatus.READY
                if (message.data != null) subtask.result = ArrayList(message.data)
            }

            else -> {
                println("error in task status")
            }
        }
    }
}