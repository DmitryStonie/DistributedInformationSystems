package org.example.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.api.requests.CrackHashRequest
import org.example.api.responses.CrackHashResponse
import org.paukov.combinatorics.CombinatoricsFactory.createPermutationWithRepetitionGenerator
import org.paukov.combinatorics.CombinatoricsFactory.createVector
import org.paukov.combinatorics.Generator
import org.paukov.combinatorics.ICombinatoricsVector
import org.springframework.web.client.RestClient
import org.springframework.web.client.body


class HashCodeCracker(val client: RestClient) {

//    suspend fun work(){
//        while(true){
//            val userInput = consoleUserInterface.enterCommand()
//            when(userInput){
//                is CrackHashInput -> {
//                    this.run(userInput)
//                }
//                else -> {}
//            }
//        }
//    }
//    suspend fun run(input: CrackHashInput): List<String>? = withContext(Dispatchers.IO) {
//        var data: List<String>? = ArrayList<String>()
//        launch {
//            val result = client.post()
//                .uri(MANAGER_CRACK_URI)
//                .body(CrackHashRequest(input.hash, input.maxLength))
//                .retrieve()
//                .body<CrackHashResponse>()
//            val id = result!!.requestId
//            while (true) {
//                var response: CrackStatusResponse?
//                try{
//                    response = client.get()
//                        .uri(MANAGER_STATUS_URI + id)
//                        .retrieve()
//                        .body<CrackStatusResponse>()
//                }
//                catch(e: Exception){
//                    println("Exception occured")
//                    break
//                }
//                consoleUserInterface.printResponse(response)
//                if (response?.status == ResponseStatus.READY.value) {
//                    data = response.data
//                    break
//                } else if (response?.status == ResponseStatus.IN_PROGRESS.value) {
//                    delay(DELAY_TIME)
//                }
//            }
//        }
//        return@withContext data
//    }

    fun getWordsIterator(numOfWorkers: Int, workerNum: Int){
        val vector: ICombinatoricsVector<String> = createVector(alphabet)
        val gen: Generator<String> = createPermutationWithRepetitionGenerator(vector, 1)
        for (perm in gen) {
            println(perm)
        }
    }
    companion object{
        val alphabet = arrayListOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
        val MANAGER_RESULT_URI: String = "http://localhost:8080/api/hash/status?requestId="

    }
}