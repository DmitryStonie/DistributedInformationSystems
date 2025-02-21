package org.example.rabbitmq.messages

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JvmRecord
data class CrackHashStatusRequest(@JsonProperty("taskId") val taskId: String): Serializable