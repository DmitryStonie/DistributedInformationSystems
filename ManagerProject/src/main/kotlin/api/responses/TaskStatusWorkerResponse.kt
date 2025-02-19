package org.example.api.responses

import org.example.core.task.TaskStatus

@JvmRecord
data class TaskStatusWorkerResponse(val status: String)