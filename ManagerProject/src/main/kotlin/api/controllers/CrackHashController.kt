package org.example.api.controllers

import org.example.Greeting
import org.example.api.requests.CrackHashRequest
import org.example.api.responses.CrackHashResponse
import org.example.api.responses.CrackStatusResponse
import org.springframework.web.bind.annotation.*
import java.util.concurrent.atomic.AtomicLong

@RestController
class CrackHashController {
    private val counter = AtomicLong()

    @PostMapping("/api/hash/crack")
    fun crackHash(@RequestBody request: CrackHashRequest): CrackHashResponse {
        return CrackHashResponse(counter.incrementAndGet().toString())
    }

    @GetMapping("/api/hash/status")
    fun crackHash(@RequestParam(value = "requestId") requestId: String?): CrackStatusResponse {
        return CrackStatusResponse("IN_PROGRESS", arrayListOf("abcd"))
    }

}