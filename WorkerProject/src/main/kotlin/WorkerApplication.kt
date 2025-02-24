package org.example

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
class WorkerApplication{

    @Bean
    fun managerExchange(): DirectExchange {
        return DirectExchange(MANAGER_EXCHANGE_NAME)
    }

    @Bean
    fun workerExchange(): DirectExchange {
        return DirectExchange(WORKER_EXCHANGE_NAME)
    }

    @Bean
    fun managerQueue(): Queue {
        return Queue(MANAGER_QUEUE_NAME)
    }
    @Bean
    fun managerStatusQueue(): Queue {
        return Queue(MANAGER_STATUS_QUEUE_NAME)
    }
    @Bean
    fun workersQueue(): Queue {
        return Queue(WORKER_QUEUE_NAME)
    }

    @Bean
    fun declareBindingManager(): Binding {
        return BindingBuilder.bind(managerQueue()).to(managerExchange()).with(MANAGER_ROUTING_KEY)
    }
    @Bean
    fun declareBindingStatusManager(): Binding {
        return BindingBuilder.bind(managerStatusQueue()).to(managerExchange()).with(MANAGER_STATUS_ROUTING_KEY)
    }

    @Bean
    fun declareBindingWorkers(): Binding {
        return BindingBuilder.bind(workersQueue()).to(workerExchange()).with(WORKER_ROUTING_KEY)
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = producerJackson2MessageConverter()
        return rabbitTemplate
    }

    @Bean
    fun producerJackson2MessageConverter(): Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter()
    }

    companion object{
        const val MANAGER_EXCHANGE_NAME: String = "managerExchange"
        const val WORKER_EXCHANGE_NAME: String = "workerExchange"
        const val MANAGER_QUEUE_NAME: String = "managerQueue"
        const val MANAGER_STATUS_QUEUE_NAME: String = "managerStatusQueue"
        const val WORKER_QUEUE_NAME: String = "workersQueue"
        const val MANAGER_ROUTING_KEY: String = "manager_routing_key"
        const val MANAGER_STATUS_ROUTING_KEY: String = "manager_status_routing_key"
        const val WORKER_ROUTING_KEY: String = "workers_routing_key"
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(WorkerApplication::class.java, *args)

}