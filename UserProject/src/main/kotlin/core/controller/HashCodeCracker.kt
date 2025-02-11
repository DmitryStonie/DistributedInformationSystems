package org.example.core.controller

import kotlinx.coroutines.*
import org.example.api.requests.CrackHashRequest
import org.example.api.responses.CrackHashResponse
import org.example.api.responses.CrackStatusResponse
import org.example.api.responses.ResponseStatus
import org.example.core.ui.ConsoleUserInterface
import org.example.core.ui.CrackHashInput
import org.example.core.ui.UserInput
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

class HashCodeCracker(val client: RestClient, val consoleUserInterface: ConsoleUserInterface) {

    suspend fun work(){
        while(true){
            val userInput = consoleUserInterface.enterCommand()
            when(userInput){
                is CrackHashInput -> {
                        this.run(userInput)
                }
                else -> {}
            }
        }
    }
    suspend fun run(input: CrackHashInput): List<String>? = withContext(Dispatchers.IO) {
        var data: List<String>? = ArrayList<String>()
        launch {
            val result = client.post()
                .uri(MANAGER_CRACK_URI)
                .body(CrackHashRequest(input.hash, input.maxLength))
                .retrieve()
                .body<CrackHashResponse>()
            val id = result!!.requestId
            while (true) {
                var response: CrackStatusResponse?
                try{
                    response = client.get()
                        .uri(MANAGER_STATUS_URI + id)
                        .retrieve()
                        .body<CrackStatusResponse>()
                }
                catch(e: Exception){
                    println("Exception occured")
                    break
                }
                consoleUserInterface.printResponse(response)
                if (response?.status == ResponseStatus.DONE.value) {
                    data = response.data
                    println(data)
                    break
                } else if (response?.status == ResponseStatus.IN_PROGRESS.value) {
                    delay(DELAY_TIME)
                }
            }
        }
        return@withContext data
    }
    companion object{
        val DELAY_TIME: Long = 1000L
        val MANAGER_CRACK_URI: String = "http://localhost:8080/api/hash/crack"
        val MANAGER_STATUS_URI: String = "http://localhost:8080/api/hash/status?requestId="
    }
}