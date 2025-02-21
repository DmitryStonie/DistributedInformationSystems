package org.example.api.client

import com.rabbitmq.client.ConnectionFactory
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class Client {
    private val client = RestClient.builder().build()
    private val log = LoggerFactory.getLogger(Client::class.java)


    fun send(){
        val factory = ConnectionFactory()
        factory.host = "localhost"
        factory.newConnection().use { connection -> connection.createChannel().use { channel ->
            channel.queueDeclare(MANAGER_QUEUE_NAME, false, false, false, null)
            val message = "Hello World!"
            channel.basicPublish("", MANAGER_QUEUE_NAME, null, message.toByteArray())
            println(" [x] Sent '$message'")




        } }
    }

    companion object {
        const val DELAY_TIME = 5000L
        const val WORKERS_QUEUE_NAME = "workersQueue"
        const val MANAGER_QUEUE_NAME = "managerQueue"
    }


}