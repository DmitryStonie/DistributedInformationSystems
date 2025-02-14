package org.example.api.responses

import org.example.core.task.TaskStatus

@JvmRecord
data class CrackStatusResponse(val status: String, val data: List<String>?)