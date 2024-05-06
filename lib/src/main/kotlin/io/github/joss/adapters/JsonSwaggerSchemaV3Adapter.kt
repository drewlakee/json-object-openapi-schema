package io.github.joss.adapters

import io.github.joss.adapters.exceptions.JsonIsNotAnObject
import io.github.joss.adapters.output.ConsoleSwaggerSchemaOutputStream
import io.github.joss.adapters.output.SwaggerSchemaOutputStream
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

open class JsonSwaggerSchemaV3Adapter(
    private val outputStream: SwaggerSchemaOutputStream = ConsoleSwaggerSchemaOutputStream(),
    private val jsonMapper: ObjectMapper = ObjectMapper()
): JsonSwaggerSchemaAdapter {

    override fun convert(json: String) {
        val node = parse(json)

        if (!node.isObject) {
            throw JsonIsNotAnObject()
        }

        if (node.size() == 0) {
            outputStream.flush("Schema:")
        }

        outputStream.flush(json)
    }

    private fun parse(json: String): JsonNode = try {
        jsonMapper.readTree(json)
    } catch (e: Exception) {
        throw JsonIsNotAnObject()
    }
}