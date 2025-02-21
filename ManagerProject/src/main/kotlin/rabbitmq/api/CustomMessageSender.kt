package org.example.rabbitmq.api

import org.example.ManagerApplication
import org.example.rabbitmq.messages.CrackHashRequest
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*


@Service
class CustomMessageSender(val rabbitTemplate: RabbitTemplate) {
//    @Scheduled(fixedDelay = 10000L)
//    fun sendMessage() {
//        val message = CrackHashRequest(UUID.randomUUID().toString(), "150f15e73422e0a5ba5b59f997fc2350", 5, 1, 1 )
//        println("Sending message...")
//        rabbitTemplate.convertAndSend(ManagerApplication.MANAGER_EXCHANGE_NAME, ManagerApplication.MANAGER_ROUTING_KEY, message)
//    }
    fun sendCrackHashRequest(message: CrackHashRequest) {
        println("Sending ${message.requestId}")
        rabbitTemplate.convertAndSend(ManagerApplication.MANAGER_EXCHANGE_NAME, ManagerApplication.MANAGER_ROUTING_KEY, message)
    }

}