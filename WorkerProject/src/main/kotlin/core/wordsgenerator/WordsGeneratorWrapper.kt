package org.example.core.wordsgenerator

import org.example.core.HashCodeCracker
import org.paukov.combinatorics.*
import java.math.BigDecimal
import java.math.BigInteger

class  WordsGeneratorWrapper(val alphabet: List<String>, val maxLength: Int, val numOfWorkers: Int, val workerNum: Int){
    private val vector: ICombinatoricsVector<String> = CombinatoricsFactory.createVector(alphabet)
    private var numOfCombinations: BigInteger
    private var currentStart: BigInteger
    private var currentEnd: BigInteger
    private var generators = ArrayList<Generator<String>>()
    private var lastIndeces = ArrayList<BigInteger>()

    private var currentGenerator: Iterator<ICombinatoricsVector<String>>
    private var currentIndex: BigInteger
    private var currentLastElementIndex: BigInteger
    private var currentGenIndex: BigInteger
    init {
        numOfCombinations = 0.toBigInteger()
        var tempNumOfPermutatuions = 1.toBigInteger()
        for(i in 1..maxLength){
            tempNumOfPermutatuions *= alphabet.size.toBigInteger()
            generators.add(CombinatoricsFactory.createPermutationWithRepetitionGenerator<String>(vector, i))
            numOfCombinations += tempNumOfPermutatuions
            lastIndeces.add(numOfCombinations)
        }
        val partSize = numOfCombinations / numOfWorkers.toBigInteger()
        if(workerNum == 1){
            currentStart = 1.toBigInteger()
        } else{
            currentStart = partSize * (workerNum - 1).toBigInteger() + 1.toBigInteger()
        }
        if(workerNum != numOfWorkers) {
            currentEnd = currentStart + partSize - 1.toBigInteger()
        } else{
            currentEnd = numOfCombinations
        }
    }
    init{
        currentLastElementIndex = lastIndeces.find { it >= currentStart }!!
        currentGenIndex = lastIndeces.indexOf(currentLastElementIndex).toBigInteger()
        currentGenerator = generators[currentGenIndex.toInt()].iterator()
        if(currentGenIndex == 0.toBigInteger()){
            currentIndex = 1.toBigInteger()
        } else{
            currentIndex = lastIndeces[(currentGenIndex - 1.toBigInteger()).toInt()] + 1.toBigInteger()
        }
        var numOfIters = currentStart - currentIndex
        while(numOfIters > 0.toBigInteger()){
            currentGenerator.iterator().next()
            numOfIters -= 1.toBigInteger()
        }
        currentIndex = currentStart
    }

    fun getNext(): String?{
        if(currentIndex > currentLastElementIndex){
            if(currentGenIndex + 1.toBigInteger() >= maxLength.toBigInteger()){
                return null
            } else{
                currentGenerator = generators[currentGenIndex.toInt() + 1].iterator()
                currentGenIndex += 1.toBigInteger()
                currentLastElementIndex = lastIndeces[currentGenIndex.toInt()]
            }
        }
        if(currentIndex > currentEnd){
            return null
        }
        val result = currentGenerator.next().vector
        currentIndex += 1.toBigInteger()
        return result.joinToString("")
    }

}