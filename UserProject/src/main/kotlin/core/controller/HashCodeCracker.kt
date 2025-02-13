package org.example.core.controller

import core.Client
import kotlinx.coroutines.*
import org.example.api.requests.CrackHashRequest
import org.example.api.responses.CrackHashResponse
import org.example.api.responses.CrackStatusResponse
import org.example.api.responses.ResponseStatus
import org.example.core.ui.ConsoleUserInterface
import org.example.core.ui.CrackHashInput
import org.example.core.ui.CrackHashStatusInput
import org.example.core.ui.UserInput
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

class HashCodeCracker(val client: Client, val consoleUserInterface: ConsoleUserInterface) {

    suspend fun work() {
        while (true) {
            val userInput = consoleUserInterface.enterCommand()
            when (userInput) {
                is CrackHashInput -> CoroutineScope(Dispatchers.Default).launch {
                    val id = client.sendCrackRequest(userInput).await()
                    if (id != null) {
                        println("requestId: ${id}")
                        println(getCrackResult(id).await())
                    } else {
                        println("Fail. Try again later.")
                    }
                }

                is CrackHashStatusInput -> CoroutineScope(Dispatchers.Default).launch {
                    val response = client.getStatus(userInput.id).await()
                    if (response != null) println("Status of job with id ${userInput.id} is ${response.status}") else println(
                        "No response"
                    )
                }
            }
        }
    }

    fun getCrackResult(id: String): Deferred<List<String>?> = CoroutineScope(Dispatchers.Default).async {
        var data: List<String>? = null
        while (true) {
            val response = client.getStatus(id).await()
            if (response?.status == ResponseStatus.DONE.value) {
                data = response.data
                break
            } else if (response?.status == ResponseStatus.IN_PROGRESS.value) {
                delay(DELAY_TIME)
            }
        }
        return@async data
    }

    companion object {
        val DELAY_TIME: Long = 1000L
        val MANAGER_CRACK_URI: String = "http://localhost:8080/api/hash/crack"
        val MANAGER_STATUS_URI: String = "http://localhost:8080/api/hash/status?requestId="
    }
}