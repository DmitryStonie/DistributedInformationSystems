package org.example.api.client

import com.rabbitmq.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException
import java.util.ArrayList

class Publisher {
    val notDeliveredMessages = ArrayList<String>()
    val factory = ConnectionFactory()
    lateinit var connection: Connection
    lateinit var channel: Channel

    init {
        factory.host = "localhost"
        CoroutineScope(Dispatchers.Default).launch {
            while(true){
                try {
                    connection = factory.newConnection()
                    channel = connection.createChannel()
                    channel.queueDeclare(MANAGER_QUEUE_NAME, true, false, false, null)
                } catch(e: Exception){
                    println("Got exception while first init rabbit")
                    delay(DELAY_TIME)
                    continue
                }
                break
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            while(true) {
                println("try to send ${notDeliveredMessages.size} und messages")
                val listIterator = notDeliveredMessages.iterator()
                while (listIterator.hasNext()) {
                    val message = listIterator.next()
                    val isPublished = publishMessageToQueue(message)
                    if (isPublished == true) {
                        listIterator.remove()
                    }
                }
                delay(DELAY_TIME)
            }
        }
    }

    private fun publishMessageToQueue(message: String): Boolean {
        try {
            channel.basicPublish(
                "",
                MANAGER_QUEUE_NAME,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                message.toByteArray()
            )
            println(" [x] Sent '$message'")
            return true
        } catch (e: Exception) {
            when (e) {
                is ConnectException, is IOException, is AlreadyClosedException, is UninitializedPropertyAccessException -> {
                    println("can't connect to rabbitmq due to not working rabbitmq")
                }
                else  -> {
                    println("$e baaad dyyying")
                }
            }
            println(" [x] Not sent '$message'")
            return false
        }
    }
    fun publishMessage(message: String){
        if(publishMessageToQueue(message) == false){
            notDeliveredMessages.add(message)
            println("messsage $message saved locally")
        }
    }

    suspend fun emulate(){
        var count = 1
        while(true){
            val message = "Hello World! $count"
            count=count + 1
            publishMessage(message)
            delay(DELAY_TIME)
        }
    }
    companion object{
        const val MANAGER_QUEUE_NAME = "managerQueue"
        const val DELAY_TIME = 5000L
    }
}