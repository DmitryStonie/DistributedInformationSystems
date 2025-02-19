package org.example.api.responses

data class CrackHashResultResponse(
    val status: Status
){
    companion object{
        enum class Status(value: String){
            OK("OK"),
            BAD("BAD")
        }
    }
}
