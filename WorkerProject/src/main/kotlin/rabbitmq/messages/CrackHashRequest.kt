package org.example.rabbitmq.messages

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JvmRecord
data class CrackHashRequest(
    @JsonProperty("requestId") val requestId: String,
    @JsonProperty("hash") val hash: String,
    @JsonProperty("maxLength") val maxLength: Int,
    @JsonProperty("numOfWorkers") val numOfWorkers: Int,
    @JsonProperty("workerNum") val workerNum: Int
) : Serializable