package org.example.api.controllers

import kotlinx.coroutines.*
import org.example.api.requests.CrackHashClientRequest
import org.example.api.responses.CrackHashResponse
import org.example.api.responses.CrackStatusResponse
import org.example.core.task.Task
import org.example.core.task.TaskStatus
import org.example.core.task.TaskUtil
import org.example.core.task.TaskVault
import org.example.mongodb.entities.TasksRepository
import org.example.rabbitmq.api.CustomMessageSender
import org.example.rabbitmq.messages.CrackHashRequest
import org.example.rabbitmq.messages.CrackHashStatusRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
class CrackHashController(val taskVault: TaskVault, val messageSender: CustomMessageSender) {

    @Value("\${workers_num}")
    lateinit var WORKERS_NUM_STR: String

    val WORKERS_NUM by lazy {
        WORKERS_NUM_STR.toInt()
    }

    private val log = LoggerFactory.getLogger(CrackHashController::class.java)

    private final val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        log.info("Exception happened ${throwable.message}")
    }
    val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)
    val taskVaultContext = newSingleThreadContext("taskVaultContext")


    @PostMapping("/api/hash/crack")
    fun crackHash(@RequestBody request: CrackHashClientRequest): CrackHashResponse {
        val id = UUID.randomUUID().toString()
        scope.launch {
            launch {
                for(i in 1..WORKERS_NUM){
                    val task = taskVault.createTask(id, WORKERS_NUM, i, request.maxLength, request.hash)
                    TaskUtil.createAndWaitTask(task, taskVault, messageSender)
                }
            }
        }
        return CrackHashResponse(id)
    }

    @GetMapping("/api/hash/status")
    fun getStatus(@RequestParam(value = "requestId") requestId: String?): CrackStatusResponse {
        var response = CrackStatusResponse(TaskStatus.ERROR.value, null)
        runBlocking {
            launch(taskVaultContext) {
                if (requestId != null){
                    val tasks = taskVault.getTasksByRequestId(requestId)
                    println("found ${tasks.size} tasks")
                    val readyTasks = tasks.filter { it.status == TaskStatus.READY }
                    val inProgressTasks = tasks.filter { it.status == TaskStatus.IN_PROGRESS }
                    val createdTasks = tasks.filter { it.status == TaskStatus.CREATED }
                    response =
                        if (inProgressTasks.size > 0 || createdTasks.size > 0) {
                            CrackStatusResponse(TaskStatus.IN_PROGRESS.value, null)
                        }else if(tasks.size == 0){
                            CrackStatusResponse(TaskStatus.NOT_SENDED.value, null)
                        }
                        else if (readyTasks.size == tasks.size) {
                            val result = ArrayList<String>()
                            for (task in readyTasks) {
                                task.result?.let { result.addAll(it) }
                            }
                            CrackStatusResponse(TaskStatus.READY.value, result)
                        } else {
                            CrackStatusResponse(TaskStatus.ERROR.value, null)
                        }
                }
            }
        }
        return response
    }

}