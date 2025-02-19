package org.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class ManagerApplication

fun main(args: Array<String>) {
    SpringApplication.run(ManagerApplication::class.java, *args)
}