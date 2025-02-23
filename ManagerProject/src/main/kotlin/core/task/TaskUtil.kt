package org.example.core.task

import kotlinx.coroutines.*
import org.example.api.controllers.CrackHashController
import org.example.rabbitmq.api.CustomMessageSender
import org.example.rabbitmq.messages.CrackHashRequest
import org.slf4j.LoggerFactory


class TaskUtil {
    companion object {
        private val log = LoggerFactory.getLogger(CrackHashController::class.java)

        private final val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            log.info("Exception happened ${throwable.message}")
        }
        val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)
        fun createAndWaitTask(
            task: Task,
            taskVault: TaskVault,
            messageSender: CustomMessageSender
        ){
            runBlocking {
                val message = CrackHashRequest(
                    task.id,
                    task.hash,
                    task.maxLength,
                    task.numOfWorkers,
                    task.workerNum
                )
                messageSender.sendCrackHashRequest(message)
            }
            scope.launch {
                waitTask(task, taskVault, messageSender)
            }
        }

        suspend fun waitTask(task: Task,
            taskVault: TaskVault,
            messageSender: CustomMessageSender
        ) {
            while (true) {
                var savedTask = taskVault.getTask(task.id)!!
                savedTask.isDied = true
                taskVault.saveTask(savedTask)
                delay(10000)
                savedTask = taskVault.getTask(task.id)!!
                if(savedTask.status == TaskStatus.CREATED){
                    println("Created task not in progress")
                    continue
                }
                if (savedTask.isDied || savedTask.status == TaskStatus.ERROR) {
                    scope.launch{
                        println("task ${task.id} died, creating new")
                        createAndWaitTask(task, taskVault, messageSender)
                    }
                    break
                } else if(savedTask.status == TaskStatus.READY){
                    break
                }
            }
        }
    }
}