package org.example.core.ui

import org.example.api.responses.CrackStatusResponse
import org.example.api.responses.ResponseStatus
import org.springframework.stereotype.Component

@Component
class ConsoleUserInterface {
    fun enterCommand(): UserInput {
        print(
            "What do you want?\n" +
                    "1. Send hash crack request\n" +
                    "2. Get hash crack status\n"
        )
        val input = readln().toInt()
        when (input) {
            1 -> {
                println("Input hash:")
                val hash = readln()
                println("Input max message length:")
                val messageLength = readln().toInt()
                return CrackHashInput(hash, messageLength)
            }

            2 -> {
                println("Input id:")
                val id = readln()
                return CrackHashStatusInput(id)
            }

            else -> throw Exception("Wrong input")
        }
    }

    fun printResponse(response: CrackStatusResponse?, id: String) {
        if (response?.status == ResponseStatus.IN_PROGRESS.value) {
            println("Work with id $id still in progress...")
        } else if (response?.status == ResponseStatus.READY.value) {
            printCrackResult(response.data, id)
        } else if (response?.status == ResponseStatus.ERROR.value){
            println("Work with id $id not done due to error. Try again later.")
        } else if (response == null) {
            println("Work with id $id got no response...")
        } else {
            println("With work with id $id Everything goddamn bad :(")
        }
    }

    fun printId(id: String){
        println("RequestId: $id")
    }

    fun printError(){
        println("Fail. Try again later.")
    }

    fun printCrackResult(result: List<String>?, id: String){
        println("Work with id $id done!\nResult: $result")
    }
}