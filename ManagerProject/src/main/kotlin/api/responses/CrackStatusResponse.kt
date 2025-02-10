package org.example.api.responses

@JvmRecord
data class CrackStatusResponse(val status: String, val data: List<String>)