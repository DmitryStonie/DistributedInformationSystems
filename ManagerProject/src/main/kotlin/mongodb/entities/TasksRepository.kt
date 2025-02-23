package org.example.mongodb.entities

import org.example.core.task.Task
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*


interface TasksRepository : MongoRepository<Task?, String?>{
    fun findByRequestId(requestId: String): List<Task>?

}