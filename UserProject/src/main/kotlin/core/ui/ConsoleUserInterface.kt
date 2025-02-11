package org.example.core.ui

import org.example.api.responses.CrackStatusResponse
import org.example.api.responses.ResponseStatus

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
                println("Input id:\n")
                val id = readln()
                return CrackHashStatusInput(id)
            }

            else -> throw Exception("Wrong input")
        }
    }

    fun printResponse(response: CrackStatusResponse?) {
        if (response?.status == ResponseStatus.IN_PROGRESS.value) {
            println("Still in progress...")
        } else if (response?.status == ResponseStatus.DONE.value) {
            println("Work done!\nResult: ${response.data}")
        } else if (response == null) {
            println("No response...")
        } else {
            println("Everything goddamn bad :(")
        }
    }
}