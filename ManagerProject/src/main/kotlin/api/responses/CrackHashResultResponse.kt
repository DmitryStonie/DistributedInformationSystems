package org.example.api.responses

data class CrackHashResult(
    val requestId: String,
    val data: List<String>
)
