package org.example.core.worker

import kotlinx.coroutines.*
import org.example.api.client.Client
import org.example.api.responses.ResponseStatus
import org.example.core.ui.ConsoleUserInterface
import org.example.core.ui.CrackHashInput
import org.example.core.ui.CrackHashStatusInput
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(0)
class Runner(private val client: Client, private val consoleUserInterface: ConsoleUserInterface) :
    CommandLineRunner {

    private val log = LoggerFactory.getLogger(Runner::class.java)

    private final val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        log.info("Exception happened ${throwable.message}")
    }
    val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)
    suspend fun work() {
        while (true) {
            try {
                when (val userInput = consoleUserInterface.enterCommand()) {
                    is CrackHashInput -> scope.launch {
                        launch {
                            val id = client.sendCrackRequest(userInput).await()
                            if (id != null) {
                                consoleUserInterface.printId(id)
                                val result = getCrackResult(id).await()
                                consoleUserInterface.printCrackResult(result, id)
                            } else {
                                consoleUserInterface.printError()
                            }
                        }
                    }

                    is CrackHashStatusInput -> scope.launch {
                        launch {
                            try {
                                val response = client.getStatus(userInput.id).await()
                                consoleUserInterface.printResponse(response, userInput.id)
                            } catch (e: Exception) {
                                log.info("manager died. try later manually.")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                log.info("Exception occured: ${e.message}")
            }
        }
    }

    private fun getCrackResult(id: String): Deferred<List<String>?> = scope.async {
        var dataInner: List<String>?
        while (true) {
            try {
                val response = client.getStatus(id).await()
                if (response?.status == ResponseStatus.READY.value) {
                    dataInner = response.data
                    break
                } else if (response?.status == ResponseStatus.ERROR.value) {
                    dataInner = response.data
                    println("Status of Task $id is ERROR. Don't wait it.")
                    break
                } else {
                    delay(DELAY_TIME)
                }
            } catch (e: Exception) {
                delay(DELAY_TIME_DIED_MANAGER)
            }
        }
        return@async dataInner
    }

    companion object {
        const val DELAY_TIME: Long = 1000L
        const val DELAY_TIME_DIED_MANAGER: Long = 10000L
    }

    override fun run(vararg args: String?) {
        runBlocking { work() }
    }
}