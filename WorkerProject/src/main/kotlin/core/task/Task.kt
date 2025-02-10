package org.example.core.task

import kotlinx.coroutines.Job

data class Task(
    val requestId: String,
    val status: TaskStatus,
    val result: List<String>,
)