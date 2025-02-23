package org.example.core.task

enum class TaskStatus(
    val value: String,
) {
    IN_PROGRESS("IN_PROGRESS"),
    READY("READY"),
    ERROR("ERROR"),
    CREATED("CREATED"),
    NOT_SENDED("NOT_SENDED")
}