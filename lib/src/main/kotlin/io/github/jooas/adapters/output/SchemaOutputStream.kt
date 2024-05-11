package io.github.jooas.adapters.output

sealed interface SchemaOutputStream {
    fun flush(schema: String)
}