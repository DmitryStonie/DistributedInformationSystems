package org.example.core.ui

class CrackHashInput(val hash: String, val maxLength: Int) : UserInput {
    override val type: Int = UserInput.Type.CrackHash.value
}