package org.example.core.task

data class Task(
    val requestId: String,
    val statuses: ArrayList<TaskStatus>,
    val result: ArrayList<String>,
)