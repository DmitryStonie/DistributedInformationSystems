package org.example

import org.example.core.HashCodeCracker
import org.example.core.HashCodeCracker.Companion.alphabet
import org.example.core.wordsgenerator.WordsGeneratorWrapper
import org.paukov.combinatorics.CombinatoricsFactory.*
import org.paukov.combinatorics.Generator
import org.paukov.combinatorics.ICombinatoricsVector
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.client.RestClient

@SpringBootApplication
class RestServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(RestServiceApplication::class.java, *args)
    println("Hello World!")

}