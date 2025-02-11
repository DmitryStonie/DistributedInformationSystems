package org.example.api.responses

enum class ResponseStatus(
    val value: String,
) {
    IN_PROGRESS("IN_PROGRESS"),
    DONE("DONE"),
    ERROR("ERROR"),
}