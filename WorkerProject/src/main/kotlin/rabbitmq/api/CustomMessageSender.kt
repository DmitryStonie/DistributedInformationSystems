package org.example.rabbitmq.api

import org.example.WorkerApplication
import org.example.rabbitmq.messages.CrackHashResponse
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service


@Service
class CustomMessageSender(private val rabbitTemplate: RabbitTemplate) {

    fun sendCrackHashResponse(message: CrackHashResponse) {
        println("Sending crack hash response ${message.requestId}   ${message.status}")
        rabbitTemplate.convertAndSend(WorkerApplication.WORKER_EXCHANGE_NAME, WorkerApplication.WORKER_ROUTING_KEY, message)
    }

}