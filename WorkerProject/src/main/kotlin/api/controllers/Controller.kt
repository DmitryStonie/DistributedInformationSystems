package org.example.api.controllers

import kotlinx.coroutines.Job
import org.example.api.requests.CrackHashRequest
import org.example.api.responses.CrackHashResponse
import org.example.api.responses.CrackStatusResponse
import org.example.core.view.TaskVault
import org.springframework.web.bind.annotation.*
import java.util.concurrent.atomic.AtomicLong

@RestController
class Controller {
    private val taskVault = TaskVault()

    @PostMapping("/api/hash/crack")
    fun crackHash(@RequestBody request: CrackHashRequest): CrackHashResponse {
        val id = counter.incrementAndGet().toString()
        val numOfWorkers = 1
        val jobs = ArrayList<Job>()
        for(i in 1..numOfWorkers){
            jobs.add()
        }

        return CrackHashResponse(id)
    }

    @GetMapping("/api/hash/status")
    fun crackHash(@RequestParam(value = "requestId") requestId: String?): CrackStatusResponse {

        return CrackStatusResponse("IN_PROGRESS", arrayListOf("abcd"))
    }

}