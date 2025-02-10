package org.example.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.core.wordsgenerator.WordsGeneratorWrapper
import org.paukov.combinatorics.CombinatoricsFactory.createPermutationWithRepetitionGenerator
import org.paukov.combinatorics.CombinatoricsFactory.createVector
import org.paukov.combinatorics.Generator
import org.paukov.combinatorics.ICombinatoricsVector
import java.math.BigInteger
import java.security.MessageDigest

class HashCodeCracker() {

    suspend fun run(hash: String, maxLength: Int, numOfWorkers: Int, workerNum: Int): List<String>? = withContext(Dispatchers.IO) {
        val data = ArrayList<String>()
        launch {
            val gen = WordsGeneratorWrapper(alphabet, maxLength, numOfWorkers, workerNum)
            var word = gen.getNext()
            while(word != null){
                if( md5(word) == hash){
                    data.add(word)
                }
                word = gen.getNext()
            }
        }

        return@withContext data
    }

    fun getWordsIterator(numOfWorkers: Int, workerNum: Int){
        val numOfCombinations =  alphabet.size.toBigInteger()
        val vector: ICombinatoricsVector<String> = createVector(alphabet)
        val gen: Generator<String> = createPermutationWithRepetitionGenerator(vector, 2)

        for (perm in gen) {
            println(perm)
        }
    }
    fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    companion object{
        val alphabet = arrayListOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
        val MANAGER_RESULT_URI: String = "http://localhost:8080/api/hash/status?requestId="

    }
}