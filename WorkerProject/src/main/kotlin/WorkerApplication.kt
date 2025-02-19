package org.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class WorkerApplication

fun main(args: Array<String>) {
    SpringApplication.run(WorkerApplication::class.java, *args)
}