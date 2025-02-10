package org.example.api.controllers

import kotlinx.coroutines.Job
import org.example.api.requests.CrackHashClientRequest
import org.example.api.responses.CrackHashResponse
import org.example.api.responses.CrackHashResult
import org.example.api.responses.CrackHashResultResponse
import org.example.api.responses.CrackStatusResponse
import org.example.core.task.TaskVault
import org.springframework.web.bind.annotation.*
import java.util.concurrent.atomic.AtomicLong

@RestController
class CrackHashController {
    private val counter = AtomicLong()
    private val taskVault = TaskVault()

    @PostMapping("/internal/api/manager/hash/crack/request")
    fun crackHash(@RequestBody request: CrackHashClientRequest): CrackHashResponse {
        val id = counter.incrementAndGet().toString()
        val numOfWorkers = 1
        val jobs = ArrayList<Job>()
        for(i in 1..numOfWorkers){
            jobs.add()
        }

        return CrackHashResponse(id)
    }

    @GetMapping("/api/hash/status")
    fun getStatus(@RequestParam(value = "requestId") requestId: String?): CrackStatusResponse {

        return CrackStatusResponse("IN_PROGRESS", arrayListOf("abcd"))
    }

    @PatchMapping("/internal/api/manager/hash/crack/request")
    fun receiveResult(@RequestBody response: CrackHashResult): CrackHashResultResponse {
        return CrackHashResultResponse("OK")
    }


}