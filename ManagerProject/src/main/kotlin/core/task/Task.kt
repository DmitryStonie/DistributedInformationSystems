package org.example.core.task

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.sql.Timestamp

@Document(collection = "tasks")
data class Task(
    val requestId: String,
    @Id
    val id: String,
    var result: ArrayList<String>?,
    val hash: String,
    var status: TaskStatus,
    var isDied: Boolean,
    val maxLength: Int,
    val numOfWorkers: Int,
    val workerNum: Int
)