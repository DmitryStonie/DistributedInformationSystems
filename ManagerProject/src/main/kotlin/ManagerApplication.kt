package org.example

import org.example.api.client.Publisher
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class ManagerApplication

suspend fun main(args: Array<String>) {
    SpringApplication.run(ManagerApplication::class.java, *args)
    val publisher = Publisher()
    publisher.emulate()
}