package org.example.api.client

import com.rabbitmq.client.*
import kotlinx.coroutines.delay
import java.io.IOException
import java.net.ConnectException


class Consumer {
    suspend fun consume() {
        val factory = ConnectionFactory()
        factory.host = "localhost"
        val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
            val message = String(delivery.body, charset("UTF-8"))
            println(" [x] Received '$message'")
        }
        val connection = factory.newConnection()
        val channel = connection.createChannel()
        channel.queueDeclare(MANAGER_QUEUE_NAME, true, false, false, null)
        channel.basicConsume(MANAGER_QUEUE_NAME, true, deliverCallback) { consumerTag: String? -> }
        println(" [*] Waiting for messages. To exit press CTRL+C")
    }
    companion object {
        const val DELAY_TIME = 5000L
        const val MANAGER_QUEUE_NAME = "managerQueue"
    }

}