package org.example.core.task

enum class TaskStatus(
    val value: String,
) {
    IN_PROGRESS("IN_PROGRESS"),
    DONE("DONE"),
    ERROR("ERROR"),
}