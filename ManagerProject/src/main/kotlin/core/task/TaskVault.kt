package org.example.core.task

import org.example.mongodb.entities.TasksRepository
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.ArrayList


@Component
class TaskVault(val repository: TasksRepository) {
    fun getTask(id: String): Task? {
        try {
            val task = repository.findById(id).get()
            return task
        } catch (e: NoSuchElementException) {
            return null
        }
    }

    fun getTasksByRequestId(requestId: String): List<Task> {
        val tasks = repository.findByRequestId(requestId) ?: return ArrayList()
        return tasks
    }

    fun saveTask(task: Task) {
        repository.save(task)
    }

    fun createTask(id: String, numOfWorkers: Int, workerNum: Int, maxLength: Int, hash: String): Task {
        val task = Task(
            id,
            UUID.randomUUID().toString(),
            null,
            hash,
            TaskStatus.CREATED,
            false,
            maxLength,
            numOfWorkers,
            workerNum
        )
        repository.save(task)
        return task
    }

    fun getTasksByStatus(taskStatus: TaskStatus): List<Task> {
        val tasks = repository.findByStatus(taskStatus) ?: ArrayList()
        return tasks
    }
}