package org.example.core.task

data class Subtask(
    val taskId: String,
    val id: String,
    var result: ArrayList<String>?,
    val hash: String,
    var status: TaskStatus,
    val maxLength: Int,
    val numOfWorkers: Int,
    val workerNum: Int

)