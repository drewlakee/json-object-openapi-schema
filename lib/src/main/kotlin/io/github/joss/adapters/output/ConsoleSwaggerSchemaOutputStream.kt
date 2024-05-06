package io.github.joss.adapters.output

class ConsoleSwaggerSchemaOutputStream: SwaggerSchemaOutputStream {

    override fun flush(schema: String) {
        println(schema)
    }
}