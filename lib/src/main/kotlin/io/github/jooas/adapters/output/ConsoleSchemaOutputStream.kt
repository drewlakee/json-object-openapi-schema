package io.github.jooas.adapters.output

class ConsoleSchemaOutputStream: SchemaOutputStream {

    override fun flush(schema: String) {
        println(schema)
    }
}