package adapters

import adapters.output.ConsoleSwaggerSchemaOutputStream
import adapters.output.SwaggerSchemaOutputStream

open class JsonSwaggerSchemaV3Adapter(
    private val outputStream: SwaggerSchemaOutputStream = ConsoleSwaggerSchemaOutputStream(),
): JsonSwaggerSchemaAdapter {

    override fun convert(json: String) {
        outputStream.flush(json)
    }
}