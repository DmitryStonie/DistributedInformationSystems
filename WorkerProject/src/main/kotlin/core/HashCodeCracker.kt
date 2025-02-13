package org.example.core

import kotlinx.coroutines.*
import org.example.core.wordsgenerator.WordsGenerator
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.MessageDigest

@Component
class HashCodeCracker {
    private val md = MessageDigest.getInstance("MD5")

    fun run(hash: String, maxLength: Int, numOfWorkers: Int, workerNum: Int): Deferred<List<String>?> =
        CoroutineScope(Dispatchers.Default).async {
            val data = ArrayList<String>()
            launch {
                val gen = WordsGenerator(alphabet, maxLength, numOfWorkers, workerNum)
                var word = gen.getNext()
                while (word != null) {
                    if (md5(word) == hash) {
                        data.add(word)
                    }
                    word = gen.getNext()
                }
            }
            return@async data
        }

    fun md5(input: String): String {
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    companion object {
        val alphabet = arrayListOf(
            "a",
            "b",
            "c",
            "d",
            "e",
            "f",
            "g",
            "h",
            "i",
            "j",
            "k",
            "l",
            "m",
            "n",
            "o",
            "p",
            "q",
            "r",
            "s",
            "t",
            "u",
            "v",
            "w",
            "x",
            "y",
            "z",
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9"
        )
    }
}