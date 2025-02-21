package org.example.api.controllers

import kotlinx.coroutines.*
import org.example.api.requests.CrackHashClientRequest
import org.example.api.responses.CrackHashResponse
import org.example.api.responses.CrackStatusResponse
import org.example.core.task.Task
import org.example.core.task.TaskStatus
import org.example.core.task.TaskVault
import org.example.mongodb.entities.TasksRepository
import org.example.rabbitmq.api.CustomMessageSender
import org.example.rabbitmq.messages.CrackHashRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
class CrackHashController(val taskVault: TaskVault, val messageSender: CustomMessageSender) {
    @Autowired
    private val repository: TasksRepository? = null

    @Value("\${workers_url}")
    lateinit var WORKERS_URL_ENV: String

    val WORKERS_URL by lazy {
        WORKERS_URL_ENV.split(' ')
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
        lateinit var task: Task
        scope.launch {
            launch {

                withContext(taskVaultContext) {
                    task = taskVault.createTask(id, WORKERS_URL.size, request.maxLength, request.hash)
                }
                repository?.save(task)
                for (i in 1..task.subtasks.size) {
                    val message = CrackHashRequest(
                        task.subtasks[i - 1].id.toString(),
                        request.hash,
                        request.maxLength,
                        task.subtasks.size,
                        i
                    )
                    messageSender.sendCrackHashRequest(message)
                }
                for (customer in repository!!.findAll()) {
                    System.out.println(customer)
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
                if (requestId != null && taskVault.getTask(requestId) != null) {
                    val task = taskVault.getTask(requestId)
                    val readyTasks = task!!.subtasks.filter { it.status == TaskStatus.READY }
                    val inProgressTasks = task.subtasks.filter { it.status == TaskStatus.IN_PROGRESS }
                    response =
                        if (inProgressTasks.size > 0) {
                            CrackStatusResponse(TaskStatus.IN_PROGRESS.value, null)
                        } else if (readyTasks.size == task.subtasks.size) {
                            val result = ArrayList<String>()
                            for (subtask in readyTasks) {
                                subtask.result?.let { result.addAll(it) }
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