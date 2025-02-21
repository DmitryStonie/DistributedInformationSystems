package org.example.core.task

data class Task(
    val id: String,
    val subtasks: ArrayList<Subtask>,
    val hash: String
)