package org.example.api.requests

@JvmRecord
data class CrackHashRequest(val hash: String, val maxLength: Short)