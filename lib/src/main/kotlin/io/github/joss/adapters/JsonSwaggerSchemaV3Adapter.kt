package io.github.joss.adapters

import io.github.joss.adapters.exceptions.JsonIsNotAnObjectException
import io.github.joss.adapters.output.ConsoleSwaggerSchemaOutputStream
import io.github.joss.adapters.output.SwaggerSchemaOutputStream
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.joss.adapters.definitions.*

open class JsonSwaggerSchemaV3Adapter(
    private val outputStream: SwaggerSchemaOutputStream = ConsoleSwaggerSchemaOutputStream(),
    private val jsonMapper: ObjectMapper = ObjectMapper()
): JsonSwaggerSchemaAdapter {

    override fun convert(json: String) {
        val node = parse(json)

        if (!node.isObject) {
            throw JsonIsNotAnObjectException()
        }

        if (node.size() == 0) {
            outputStream.flush("Schema:")
        }

        // getSchemaProperties(node)

        outputStream.flush(json)
    }

    private fun parse(json: String): JsonNode = try {
        jsonMapper.readTree(json)
    } catch (e: Exception) {
        throw JsonIsNotAnObjectException()
    }
}