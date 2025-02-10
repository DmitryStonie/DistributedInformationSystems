package org.example.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.api.requests.CrackHashClientRequest
import org.example.api.responses.CrackHashResponse
import org.example.api.responses.CrackStatusResponse
import org.springframework.web.client.body

class HashCodeCracker {
    suspend fun run(): List<String>? = withContext(Dispatchers.IO) {
        var data: List<String>? = ArrayList<String>()
        launch {
            val result = client.post()
                .uri(MANAGER_CRACK_URI)
                .body(CrackHashClientRequest(input.hash, input.maxLength))
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
                if (response?.status == ResponseStatus.READY.value) {
                    data = response.data
                    break
                } else if (response?.status == ResponseStatus.IN_PROGRESS.value) {
                    delay(DELAY_TIME)
                }
            }
        }
        return@withContext data
    }
}