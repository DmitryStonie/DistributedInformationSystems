package api.responses

enum class ResponseStatus(
    val value: String,
) {
    IN_PROGRESS("IN_PROGRESS"),
    READY("READY"),
    ERROR("ERROR"),
}