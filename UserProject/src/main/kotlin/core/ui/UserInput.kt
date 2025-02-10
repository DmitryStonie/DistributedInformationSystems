package org.example.core.ui

sealed interface UserInput {
    enum class Type(
        val value: Int,
    ) {
        CrackHash(1),
        CrackHashStatus(2),
    }

    val type: Int
}