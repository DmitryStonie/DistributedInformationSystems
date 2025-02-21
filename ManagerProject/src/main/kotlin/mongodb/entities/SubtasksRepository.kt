package org.example.mongodb.entities

import org.example.core.task.Subtask
import org.example.core.task.Task
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*


interface SubtasksRepository : MongoRepository<Subtask?, String?> {
    fun findById(id: UUID): Subtask?
    fun findByTaskId(taskId: UUID): Subtask?
}