package org.example

import org.example.api.client.Client
import core.worker.HashCodeCracker
import org.example.core.ui.ConsoleUserInterface
import org.springframework.web.client.RestClient

suspend fun main() {
    val restClient = RestClient
        .builder()
        .build()
    val client = Client(restClient)
    val cracker = HashCodeCracker(client, ConsoleUserInterface())
    cracker.work()
}