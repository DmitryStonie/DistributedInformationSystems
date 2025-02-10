package org.example.core.task

import kotlinx.coroutines.Job

data class Task(
    val requestId: String,
    val statuses: ArrayList<String>,
    val result: ArrayList<String>,
)