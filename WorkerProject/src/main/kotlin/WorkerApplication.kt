package org.example

import com.rabbitmq.client.*
import org.example.api.client.Consumer.Companion.MANAGER_QUEUE_NAME
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.nio.charset.StandardCharsets


@SpringBootApplication
class WorkerApplication

suspend fun main(args: Array<String>) {
    SpringApplication.run(WorkerApplication::class.java, *args)
//    val consumer = Consumer()
//    consumer.consume()
    val factory = ConnectionFactory()
    factory.host = "localhost"
    val connection: Connection = factory.newConnection()
    val channel: Channel = connection.createChannel()

    channel.queueDeclare(MANAGER_QUEUE_NAME, true, false, false, null)
    println(" [*] Waiting for messages. To exit press CTRL+C")

    val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
        val message = String(delivery.body, StandardCharsets.UTF_8)
        println(" [x] Received '$message'")
    }
    channel.basicConsume(MANAGER_QUEUE_NAME, true, deliverCallback) { consumerTag -> }
}