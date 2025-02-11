package org.example.core.task

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.api.requests.CrackHashResultRequest
import org.example.api.responses.CrackHashWorkerResponse
import org.example.core.HashCodeCracker
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

class Task(
    val requestId: String,
    val status: TaskStatus,
    val result: MutableList<String>,
) {
    suspend fun run(hash: String, maxLength: Int, numOfWorkers: Int, workerNum: Int) = coroutineScope {
        launch {
            val res = HashCodeCracker().run(hash, maxLength, numOfWorkers, workerNum)
            res?.let { result.addAll(it) }
            println("res ${res}")
            val client = RestClient.builder()
                .baseUrl("https://example.com")
                .defaultUriVariables(mapOf("variable" to "foo"))
                .defaultHeader("My-Header", "Foo")
                .defaultCookie("My-Cookie", "Bar")
                .build()
            var response = client.patch()
                .uri("http://localhost:8080/internal/api/manager/hash/crack/request")
                .body(CrackHashResultRequest(requestId, result))
                .retrieve()
                .body<CrackHashWorkerResponse>()
            if (response?.status == "BAD") {
                while (response?.status == "BAD") {
                    delay(1000L)
                    response = client.patch()
                        .uri("http://localhost:8080/internal/api/manager/hash/crack/request")
                        .body(CrackHashResultRequest(requestId, result))
                        .retrieve()
                        .body<CrackHashWorkerResponse>()
                }
            }
        }
    }
}