package io.github.joss.adapters.output

interface SwaggerSchemaOutputStream {
    fun flush(schema: String)
}