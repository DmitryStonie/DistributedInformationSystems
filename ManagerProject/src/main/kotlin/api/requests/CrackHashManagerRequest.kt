package org.example.api.requests

@JvmRecord
data class CrackHashManagerRequest(val requestId: String, val hash: String, val maxLength: Int, val numOfWorkers: Int, val workerNum: Int)