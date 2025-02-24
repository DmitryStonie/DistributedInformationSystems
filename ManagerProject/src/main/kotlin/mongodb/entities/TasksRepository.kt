package org.example.mongodb.entities

import org.example.core.task.Task
import org.example.core.task.TaskStatus
import org.springframework.data.mongodb.repository.MongoRepository


interface TasksRepository : MongoRepository<Task?, String?> {
    fun findByRequestId(requestId: String): List<Task>?
    fun findByStatus(status: TaskStatus): List<Task>?

}