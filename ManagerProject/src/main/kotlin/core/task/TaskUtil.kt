package org.example.core.task

import kotlinx.coroutines.*
import org.example.api.controllers.CrackHashController
import org.example.rabbitmq.api.CustomMessageSender
import org.example.rabbitmq.messages.CrackHashRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class TaskUtil(val taskVault: TaskVault, val messageSender: CustomMessageSender) {

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
                    println("rabbitmq is unavailable.")
                    task.status = TaskStatus.NOT_SENDED
                    taskVault.saveTask(task)
                }
            }
            scope.launch {
                waitTask(task, message, taskVault, messageSender)
            }
        }

        suspend fun waitTask(
            task: Task,
            message: CrackHashRequest,
            taskVault: TaskVault,
            messageSender: CustomMessageSender
        ) {
            while (true) {
                println("start")
                var savedTask = taskVault.getTask(task.id)!!
                savedTask.isDied = true
                taskVault.saveTask(savedTask)
                delay(10000)
                savedTask = taskVault.getTask(task.id)!!
                if (savedTask.status == TaskStatus.NOT_SENDED) {
                    println("send unsended task ${task.id} ${message}")
                    CoroutineScope(Dispatchers.Default).launch{
                        try{
                            messageSender.sendCrackHashRequest(message)
                        } catch (e: Exception){
                            println("rabbitmq still dead")
                        }
                    }
                    println("continue")
                    continue
                }
                if (savedTask.status == TaskStatus.CREATED) {
                    println("Created task not in progress")
                    continue
                }
                if (savedTask.isDied || savedTask.status == TaskStatus.ERROR) {
                    scope.launch {
                        println("task ${task.id} died, creating new")
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
        println("aasd")
        val tasks = taskVault.getTasksByStatus(TaskStatus.NOT_SENDED)
        println("got unsended atasks ${tasks.size}")
        for(task in tasks){
            createAndWaitTask(task, taskVault, messageSender)
        }
        val createdTasks = taskVault.getTasksByStatus(TaskStatus.CREATED)
        for(task in createdTasks){
            createAndWaitTask(task, taskVault, messageSender)
        }
        val inProgressTasks = taskVault.getTasksByStatus(TaskStatus.IN_PROGRESS)
        for(task in inProgressTasks){
            if(task.isDied == true)
                createAndWaitTask(task, taskVault, messageSender)
        }
    }
}