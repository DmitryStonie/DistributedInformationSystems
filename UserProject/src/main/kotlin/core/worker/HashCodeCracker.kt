package core.worker

import org.example.api.client.Client
import kotlinx.coroutines.*
import org.example.api.responses.ResponseStatus
import org.example.core.ui.ConsoleUserInterface
import org.example.core.ui.CrackHashInput
import org.example.core.ui.CrackHashStatusInput

class HashCodeCracker(private val client: Client, private val consoleUserInterface: ConsoleUserInterface) {

    suspend fun work() {
        while (true) {
            when (val userInput = consoleUserInterface.enterCommand()) {
                is CrackHashInput -> CoroutineScope(Dispatchers.Default).launch {
                    val id = client.sendCrackRequest(userInput).await()
                    if (id != null) {
                        consoleUserInterface.printId(id)
                        val result = getCrackResult(id).await()
                        consoleUserInterface.printCrackResult(result, id)
                    } else {
                        consoleUserInterface.printError()
                    }
                }

                is CrackHashStatusInput -> CoroutineScope(Dispatchers.Default).launch {
                    val response = client.getStatus(userInput.id).await()
                    consoleUserInterface.printResponse(response, userInput.id)
                }
            }
        }
    }

    private fun getCrackResult(id: String): Deferred<List<String>?> = CoroutineScope(Dispatchers.Default).async {
        val data: List<String>?
        while (true) {
            val response = client.getStatus(id).await()
            if (response?.status == ResponseStatus.DONE.value) {
                data = response.data
                break
            } else if (response?.status == ResponseStatus.IN_PROGRESS.value) {
                delay(DELAY_TIME)
            }
        }
        return@async data
    }

    companion object {
        const val DELAY_TIME: Long = 1000L
    }
}