package org.example.core.task

import kotlinx.coroutines.Job

data class Task(
    val requestId: String,
    val jobs: List<Job>,
    val result: List<String>,
)