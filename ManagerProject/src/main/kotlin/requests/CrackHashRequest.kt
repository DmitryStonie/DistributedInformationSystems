package org.example.requests

@JvmRecord
data class CrackHashRequest(val hash: String, val maxLength: Short)