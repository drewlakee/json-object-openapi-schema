package io.github.joss.adapters.output

interface SchemaOutputStream {
    fun flush(schema: String)
}