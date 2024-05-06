package adapters.output

interface SwaggerSchemaOutputStream {
    fun flush(schema: String)
}