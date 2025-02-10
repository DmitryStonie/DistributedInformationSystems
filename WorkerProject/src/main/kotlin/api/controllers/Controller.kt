package org.example.api.controllers

import kotlinx.coroutines.Job
import org.example.api.requests.CrackHashRequest
import org.example.api.responses.CrackHashResponse
import org.example.core.task.Task
import org.example.core.task.TaskStatus
import org.example.core.task.TaskVault
import org.springframework.web.bind.annotation.*
import java.util.concurrent.atomic.AtomicLong

@RestController
class Controller {
    private val taskVault = TaskVault()

    @PostMapping("/internal/api/worker/hash/crack/task")
    fun crackHash(@RequestBody request: CrackHashRequest): CrackHashResponse {

        val task = Task(request.requestId, TaskStatus.IN_PROGRESS, ArrayList<String>())
//        launch {
//
//        }
//
//        val id = counter.incrementAndGet().toString()
//        val numOfWorkers = 1
//        val jobs = ArrayList<Job>()
//        for(i in 1..numOfWorkers){
//            jobs.add()
//        }

        return CrackHashResponse("aaa")
    }


}