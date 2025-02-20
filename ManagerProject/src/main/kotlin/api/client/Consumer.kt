package org.example.api.client

import com.rabbitmq.client.*
import org.example.api.client.Client.Companion.WORKERS_QUEUE_NAME


class Consumer {
    fun consume(){
        val factory = ConnectionFactory()
        factory.host = "localhost"
        val connection: Connection = factory.newConnection()
        val channel: Channel = connection.createChannel()

        channel.queueDeclare(WORKERS_QUEUE_NAME, false, false, false, null)
        println(" [*] Waiting for messages. To exit press CTRL+C")

        val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
            val message = String(delivery.body, charset("UTF-8"))
            println(" [x] Received '$message'")
        }
        channel.basicConsume(WORKERS_QUEUE_NAME, true, deliverCallback) { consumerTag: String? -> }
    }

}