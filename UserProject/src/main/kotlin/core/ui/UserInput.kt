package org.example.core.ui

sealed interface UserInput {
    enum class Type(
        val value: Int,
    ) {
        CrackHash(1),
        GetCrackHashStatus(2),
    }

    val type: Int
}