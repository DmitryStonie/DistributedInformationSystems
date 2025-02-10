package org.example.api.requests

@JvmRecord
data class CrackHashClientRequest(val hash: String, val maxLength: Int)