package org.example.rabbitmq.messages

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable


@JvmRecord
data class CrackHashResponse(
    @JsonProperty("status") val status: String,
    @JsonProperty("requestId") val requestId: String,
    @JsonProperty("data") val data: List<String>?
) : Serializable