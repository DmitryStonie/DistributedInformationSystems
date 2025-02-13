package org.example.api.responses

@JvmRecord
data class CrackHashResultResponse(val status: Status){

    companion object{
        enum class Status(value: String){
            OK("OK"),
            BAD("BAD")
        }
    }
}