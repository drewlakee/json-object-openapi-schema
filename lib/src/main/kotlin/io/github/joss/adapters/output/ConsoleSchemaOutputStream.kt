package io.github.joss.adapters.output

class ConsoleSchemaOutputStream: SchemaOutputStream {

    override fun flush(schema: String) {
        println(schema)
    }
}