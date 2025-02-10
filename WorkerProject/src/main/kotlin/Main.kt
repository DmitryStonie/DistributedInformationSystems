package org.example

import org.example.core.HashCodeCracker
import org.springframework.web.client.RestClient

fun main() {
    println("Hello World!")
    val customClient = RestClient.builder()
        .baseUrl("https://example.com")
        .defaultUriVariables(mapOf("variable" to "foo"))
        .defaultHeader("My-Header", "Foo")
        .defaultCookie("My-Cookie", "Bar")
        .build()
    HashCodeCracker(customClient).getWordsIterator(1, 1)


}