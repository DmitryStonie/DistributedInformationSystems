package org.example.core.task

import kotlinx.coroutines.*
import org.example.api.controllers.CrackHashController
import org.example.rabbitmq.api.CustomMessageSender
import org.example.rabbitmq.messages.CrackHashRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class TaskUtil(val taskVault: TaskVault, val messageSender: CustomMessageSender) {

    companion object {
        private val log = LoggerFactory.getLogger(CrackHashController::class.java)

        private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            log.info("Exception happened ${throwable.message}")
        }
        private val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)

        fun createAndWaitTask(
            task: Task,
            taskVault: TaskVault,
            messageSender: CustomMessageSender
        ) {
            val message = CrackHashRequest(
                task.id,
                task.hash,
                task.maxLength,
                task.numOfWorkers,
                task.workerNum
            )
            runBlocking {
                try {
                    messageSender.sendCrackHashRequest(message)
                } catch (e: Exception) {
                    log.info("rabbitmq is unavailable.")
                    task.status = TaskStatus.NOT_SENDED
                    taskVault.saveTask(task)
                }
            }
            scope.launch {
                waitTask(task, message, taskVault, messageSender)
            }
        }

        private suspend fun waitTask(
            task: Task,
            message: CrackHashRequest,
            taskVault: TaskVault,
            messageSender: CustomMessageSender
        ) {
            while (true) {
                var savedTask = taskVault.getTask(task.id)!!
                savedTask.isDied = true
                taskVault.saveTask(savedTask)
                delay(10000)
                savedTask = taskVault.getTask(task.id)!!
                if (savedTask.status == TaskStatus.NOT_SENDED) {
                    log.info("send unsended task ${task.id} $message")
                    CoroutineScope(Dispatchers.Default).launch {
                        try {
                            messageSender.sendCrackHashRequest(message)
                        } catch (e: Exception) {
                            log.error("rabbitmq still dead")
                        }
                    }
                    continue
                }
                if (savedTask.status == TaskStatus.CREATED) {
                    log.info("Created task not in progress")
                    continue
                }
                if (savedTask.isDied || savedTask.status == TaskStatus.ERROR) {
                    scope.launch {
                        log.info("task ${task.id} died, creating new")
                        createAndWaitTask(task, taskVault, messageSender)
                    }
                    break
                } else if (savedTask.status == TaskStatus.READY) {
                    break
                }
            }
        }
    }

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationEvent() {
        val tasks = taskVault.getTasksByStatus(TaskStatus.NOT_SENDED)
        for (task in tasks) {
            createAndWaitTask(task, taskVault, messageSender)
        }
        val createdTasks = taskVault.getTasksByStatus(TaskStatus.CREATED)
        for (task in createdTasks) {
            createAndWaitTask(task, taskVault, messageSender)
        }
        val inProgressTasks = taskVault.getTasksByStatus(TaskStatus.IN_PROGRESS)
        for (task in inProgressTasks) {
            if (task.isDied)
                createAndWaitTask(task, taskVault, messageSender)
        }
    }
}