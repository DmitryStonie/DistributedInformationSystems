package org.example

import org.example.mongodb.entities.TasksRepository
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
@EnableMongoRepositories
class ManagerApplication{
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
        val MANAGER_EXCHANGE_NAME: String = "managerExchange"
        val WORKER_EXCHANGE_NAME: String = "workerExchange"
        val MANAGER_QUEUE_NAME: String = "managerQueue"
        val MANAGER_STATUS_QUEUE_NAME: String = "managerStatusQueue"
        val WORKER_QUEUE_NAME: String = "workersQueue"
        val MANAGER_ROUTING_KEY: String = "manager_routing_key"
        val MANAGER_STATUS_ROUTING_KEY: String = "manager_status_routing_key"
        val WORKER_ROUTING_KEY: String = "workers_routing_key"
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(ManagerApplication::class.java, *args)
}