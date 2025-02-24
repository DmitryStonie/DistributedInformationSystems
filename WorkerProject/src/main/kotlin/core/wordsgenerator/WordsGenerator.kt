package org.example.core.wordsgenerator

import org.paukov.combinatorics.*
import java.math.BigInteger

class WordsGenerator(alphabet: List<String>, private val maxLength: Int, numOfWorkers: Int, workerNum: Int) {
    private val vector: ICombinatoricsVector<String> = CombinatoricsFactory.createVector(alphabet)
    private var numOfCombinations: BigInteger = 0.toBigInteger()
    private var currentStart: BigInteger
    private var currentEnd: BigInteger
    private var generators = ArrayList<Generator<String>>()
    private var lastIndexes = ArrayList<BigInteger>()

    private var currentGenerator: Iterator<ICombinatoricsVector<String>>
    private var currentIndex: BigInteger
    private var currentLastElementIndex: BigInteger
    private var currentGenIndex: BigInteger

    init {
        var tempNumanPermutations = 1.toBigInteger()
        for (i in 1..maxLength) {
            tempNumanPermutations *= alphabet.size.toBigInteger()
            generators.add(CombinatoricsFactory.createPermutationWithRepetitionGenerator(vector, i))
            numOfCombinations += tempNumanPermutations
            lastIndexes.add(numOfCombinations)
        }
        val partSize = numOfCombinations / numOfWorkers.toBigInteger()
        currentStart = if (workerNum == 1) {
            1.toBigInteger()
        } else {
            partSize * (workerNum - 1).toBigInteger() + 1.toBigInteger()
        }
        currentEnd = if (workerNum != numOfWorkers) {
            currentStart + partSize - 1.toBigInteger()
        } else {
            numOfCombinations
        }
    }

    init {
        currentLastElementIndex = lastIndexes.find { it >= currentStart }!!
        currentGenIndex = lastIndexes.indexOf(currentLastElementIndex).toBigInteger()
        currentGenerator = generators[currentGenIndex.toInt()].iterator()
        currentIndex = if (currentGenIndex == 0.toBigInteger()) {
            1.toBigInteger()
        } else {
            lastIndexes[(currentGenIndex - 1.toBigInteger()).toInt()] + 1.toBigInteger()
        }
        var numOfIters = currentStart - currentIndex
        while (numOfIters > 0.toBigInteger()) {
            currentGenerator.iterator().next()
            numOfIters -= 1.toBigInteger()
        }
        currentIndex = currentStart
    }

    fun getNext(): String? {
        if (currentIndex > currentLastElementIndex) {
            if (currentGenIndex + 1.toBigInteger() >= maxLength.toBigInteger()) {
                return null
            } else {
                currentGenerator = generators[currentGenIndex.toInt() + 1].iterator()
                currentGenIndex += 1.toBigInteger()
                currentLastElementIndex = lastIndexes[currentGenIndex.toInt()]
            }
        }
        if (currentIndex > currentEnd) {
            return null
        }
        val result = currentGenerator.next().vector
        currentIndex += 1.toBigInteger()
        return result.joinToString("")
    }

}