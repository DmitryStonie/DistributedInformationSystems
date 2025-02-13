package org.example.api.requests


@JvmRecord
data class CrackHashResultRequest(val requestId: String, val data: List<String>?, val workerNum: Int)