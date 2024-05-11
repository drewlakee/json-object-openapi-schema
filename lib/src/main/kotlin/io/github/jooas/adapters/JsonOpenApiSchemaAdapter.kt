package io.github.jooas.adapters

import io.github.jooas.adapters.input.JsonInputStream
import io.github.jooas.adapters.output.SchemaOutputStream

interface JsonOpenApiSchemaAdapter {
    fun convert(json: String): String
    fun convert(input: JsonInputStream, output: SchemaOutputStream)
}