package org.example.rabbitmq.api

import org.example.ManagerApplication
import org.example.rabbitmq.messages.CrackHashRequest
import org.example.rabbitmq.messages.CrackHashStatusRequest
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*


@Service
class CustomMessageSender(val rabbitTemplate: RabbitTemplate) {
    fun sendCrackHashRequest(message: CrackHashRequest) {
        println("Sending ${message.requestId}")
        rabbitTemplate.convertAndSend(ManagerApplication.MANAGER_EXCHANGE_NAME, ManagerApplication.MANAGER_ROUTING_KEY, message)
    }

    fun sendCrackHashStatus(message: CrackHashStatusRequest) {
        println("Sending ${message.taskId} status request")
        rabbitTemplate.convertAndSend(ManagerApplication.MANAGER_EXCHANGE_NAME, ManagerApplication.MANAGER_STATUS_ROUTING_KEY, message)
    }

}