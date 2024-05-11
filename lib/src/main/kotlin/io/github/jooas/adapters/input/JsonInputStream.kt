package io.github.jooas.adapters.input

sealed interface JsonInputStream {
    fun get(): String
}