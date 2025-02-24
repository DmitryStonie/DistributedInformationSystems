package org.example.rabbitmq.api

import org.example.ManagerApplication
import org.example.rabbitmq.messages.CrackHashRequest
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service


@Service
class CustomMessageSender(val rabbitTemplate: RabbitTemplate) {
    private val log = LoggerFactory.getLogger(CustomMessageSender::class.java)
    fun sendCrackHashRequest(message: CrackHashRequest) {
        log.info("Sending ${message.requestId}")
        rabbitTemplate.convertAndSend(
            ManagerApplication.MANAGER_EXCHANGE_NAME,
            ManagerApplication.MANAGER_ROUTING_KEY,
            message
        )
    }

}